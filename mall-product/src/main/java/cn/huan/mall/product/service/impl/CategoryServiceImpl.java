package cn.huan.mall.product.service.impl;

import cn.huan.common.constant.RedisKey;
import cn.huan.common.utils.PageUtils;
import cn.huan.common.utils.Query;
import cn.huan.mall.product.dao.CategoryDao;
import cn.huan.mall.product.entity.CategoryEntity;
import cn.huan.mall.product.service.CategoryBrandRelationService;
import cn.huan.mall.product.service.CategoryService;
import cn.huan.mall.product.vo.Catalog3List;
import cn.huan.mall.product.vo.Catelog2List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取所有分类信息以及子分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithChildren() {
        //获取所有分类信息
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<>());
        //获取所有一级分类
        List<CategoryEntity> root = categoryEntities.stream()
                .filter(category -> category.getParentCid() == 0)
                .map(categoryEntity -> getWithChildren(categoryEntity, categoryEntities))
                .sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort())))
                .collect(Collectors.toList());
        return root;
    }

    @Override
    public void removeByCids(List<Long> cIds) {
        //TODO 删除功能实现
        baseMapper.deleteBatchIds(cIds);
    }

    @Override
    public Long[] getCategoryPath(Long catelogId) {
        List<Long> categoryPath = new ArrayList<>();

        return categoryPath(catelogId, categoryPath).toArray(new Long[categoryPath.size()]);
    }

    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        //更新自己
        baseMapper.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Cacheable(value = {"category"})
    @Override
    public List<CategoryEntity> getChildren(Long parentId) {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", parentId));
        return categoryEntities;

    }

    public List<CategoryEntity> getChildren(Long parentId, List<CategoryEntity> all) {
        if (!CollectionUtils.isEmpty(all)) {
            return all.stream().filter(item -> item.getParentCid() == parentId).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Map<String, List<Catelog2List>> catalog2List() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String catalogJson = ops.get(RedisKey.PRODUCT_CATEGORY_JSON);
        if ("true".equals(catalogJson)) return null;
        if (StringUtils.isEmpty(catalogJson)) {
//            这里如果数据库没有值（map.isEmpty()）时，百万并发进来全部查询数据库，数据库中也没有值，导致了缓存穿透，解决：
//            1.数据库查到空值，可以将空值存到缓存加一个标志位，下次进缓存看到有值，并且是给定的标志位，则不用去查询数据库。
//            2.设置一个布隆过滤器，将值传进去，如果返回的是false，代表数据库中肯定没有该数据，否则不一定有（过滤了一些必然为空的请求）
//            如果同一时间大量的Key全部失效，并且该时刻大并发同时访问，也会导致缓存雪崩问题，解决：Key设定不同的有效期
//            如果一个热点Key失效的瞬间，大并发来访问该热点Key，会导致缓存击穿，解决，加锁一个人先查。
//            判断StringUtils.isEmpty(catalogJson)成立的时候第一个线程进来，立马加锁，查到过以后写入缓存，释放锁
//            其他人拿到锁以后先判断缓存中有没有，如果有直接取缓存中的。
//            1.使用本地锁synchronized，只能锁住当前容器。大并发进来每个容器都要查询数据库
//            2.使用分布式锁
//            Since the SET command options can replace SETNX, SETEX, PSETEX,
//            it is possible that in future versions of Redis these three
//            commands will be deprecated and finally removed.
//            使用SET命令可以取代以上三个，这三个在未来将被移除
//            2.1 分布式锁演进版本1 让所有线程去redis抢锁，setNX -- Only set the key if it does not already exist.
//            存在问题，如果运行期间服务器宕机了，锁永远得不到释放，这样就会导致死锁，解决方案，给每个锁设置有效期
//            2.2 分布式锁演进版本2 给每个锁设置有效期，到期自动释放
//            存在问题，如果还没运行到设置有效期宕机了，锁还是得不到释放，造成死锁
//            2.3 分布式锁演进版本3 保证拿到锁和设置有效期是一个原子操作。setnx ex
//            存在问题，业务还没执行完，锁过期了，别的线程进来抢到锁，最后该线程执行完了把别的现成的锁给删了，导致其他线程又开始抢锁
//            2.4 分布式锁演进版本4 只删自己的锁，设置锁的值为uuid+当前线程号，删除锁的时候判断是自己的锁才删除。
//            存在问题 判断锁是自己过后锁立马过期了，然后别的线程抢占了锁，删除的还是别人的锁
//            2.5 分布式锁演进版本5 保证查询和删除也是原子性，这里官方给了解释
//            It is possible to make this system more robust modifying the unlock schema as follows:
//             Instead of setting a fixed string, set a non-guessable large random string, called token.
//            使用随机字符串取代锁的值（2.4）
//             Instead of releasing the lock with DEL, send a script that only removes the key if the value matches.
//            发送lua脚本存在则删除，不能用自带的删除，脚本如下
//            if redis.call("get",KEYS[1]) == ARGV[1]
//            then
//                return redis.call("del",KEYS[1])
//            else
//                return 0
//            end
//            2.6 分布式锁演进版本6 使用redisson框架，可以自动续期办保证业务执行完毕
            /*
            1.导入依赖
            <dependency>
                <groupId>org.redisson</groupId>
                <artifactId>redisson</artifactId>
                <version>3.13.4</version>
            </dependency>
            2.配置配置文件 将生成RedissonClient
            // 1. Create config object
                Config config = new Config();
                config.useClusterServers()
                       // use "rediss://" for SSL connection
                      .addNodeAddress("redis://127.0.0.1:7181");

                // or read config from file
                config = Config.fromYAML(new File("config-file.yaml"));
                // 2. Create Redisson instance

                // Sync and Async API
                RedissonClient redisson = Redisson.create(config);

                // Reactive API
                RedissonReactiveClient redissonReactive = Redisson.createReactive(config);

                // RxJava2 API
                RedissonRxClient redissonRx = Redisson.createRx(config);
             */
            System.out.println("缓存中没有该数据，开始查询数据库。。。");
            Map<String, List<Catelog2List>> catalog = catalog2ListRedisson();
            return catalog;
        }
        //将catalogJson转为对象
        System.out.println("没经过锁，直接查缓存");
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2List>>>() {
        });
    }

    /**
     * 使用redisson框架实现分布式锁
     *
     * @return
     */
    public Map<String, List<Catelog2List>> catalog2ListRedisson() {
        RLock lock = redissonClient.getLock("lock");
        //这里不设置有效期，默认30秒，使用WatchDog监听当有效期剩2 / 3（20）秒时自动续到30秒
        //设置有效期 则不自动续期
        lock.lock();
        try {
            return afterGetLock();
        } finally {
            lock.unlock();
        }
    }

    /**
     * redis实现分布式锁
     *
     * @return
     */
    public Map<String, List<Catelog2List>> catalog2ListRedisLock() {
        //使用setnx抢锁
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //抢锁并设置有效期
        String uuid = UUID.randomUUID().toString();
        if (!ops.setIfAbsent("lock", uuid + Thread.currentThread().getId(), 30, TimeUnit.SECONDS)) {
            //没抢到锁，自旋等待
            try {
                Thread.sleep(100);
                catalog2ListRedisLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //设置有效期30s
//            redisTemplate.expire("lock",30, TimeUnit.SECONDS);
            return afterGetLock();
        } finally {
//            if((uuid + Thread.currentThread().getId()).equals(ops.get("lock"))){
//                redisTemplate.delete("lock");
//            }
            //使用脚本删除锁
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            String key = uuid + Thread.currentThread().getId();
            redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"), key);
        }
    }

    /**
     * 使用本地锁
     *
     * @return
     */
    public Map<String, List<Catelog2List>> catalog2ListLocalLock() {
        //因为spring是单实例的所以锁住this就可以锁住其他对象
        synchronized (this) {
            //判断缓存中有没有
            return afterGetLock();
        }
    }

    private Map<String, List<Catelog2List>> afterGetLock() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String catalogJson = ops.get(RedisKey.PRODUCT_CATEGORY_JSON);
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存中有，直接返回缓存中的
            System.out.println("经过锁，缓存中有，查缓存");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2List>>>() {
            });
        }
        //真正查询数据库
        System.out.println("查询数据库中。。。");
        Map<String, List<Catelog2List>> catalog = catalog2ListFromDb();
        if (CollectionUtils.isEmpty(catalog)) {
            ops.set(RedisKey.PRODUCT_CATEGORY_JSON, "true");
        }
        //查到以后，放入缓存
        String jsonString = JSON.toJSONString(catalog);
        ops.set(RedisKey.PRODUCT_CATEGORY_JSON, jsonString);
        return catalog;
    }

    public Map<String, List<Catelog2List>> catalog2ListFromDb() {
        Map<String, List<Catelog2List>> map = new HashMap<>(); //查出所有的一级分类
        //查出所有的分类信息
        List<CategoryEntity> all = baseMapper.selectList(null);
        List<CategoryEntity> categoryLevel1List = getChildren(0L, all);
        if (!CollectionUtils.isEmpty(categoryLevel1List)) {
            List<Long> catIds = categoryLevel1List.stream().map(entity -> entity.getCatId()).collect(Collectors.toList());
            catIds.forEach(id -> {
                //查询该一级分类下所有的二级分类信息
                List<CategoryEntity> categoryLevel2List = getChildren(id, all);
                List<Catelog2List> catelog2Lists = null;
                if (!CollectionUtils.isEmpty((categoryLevel2List))) {
                    catelog2Lists = categoryLevel2List.stream().map(categoryLevel2 -> {
                        Long catId = categoryLevel2.getCatId();
                        //获取三级分类
                        List<CategoryEntity> categoryLevel3List = getChildren(catId, all);
                        List<Catalog3List> catelog3Lists = null;
                        if (!CollectionUtils.isEmpty(categoryLevel3List)) {
                            catelog3Lists = categoryLevel3List.stream().map(categoryLevel3 -> {
                                return new Catalog3List(catId, categoryLevel3.getCatId(), categoryLevel3.getName());
                            }).collect(Collectors.toList());
                        }
                        return new Catelog2List(id, catelog3Lists, catId, categoryLevel2.getName());
                    }).collect(Collectors.toList());
                }
                map.put(id.toString(), catelog2Lists);
            });
        }
        return map;
    }

    public List<Long> categoryPath(Long catelogId, List<Long> categoryPath) {
        if (catelogId == 0) return categoryPath;
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        categoryPath(categoryEntity.getParentCid(), categoryPath);
        categoryPath.add(catelogId);
        return categoryPath;
    }

    /**
     * 递归获取所有的分类及其子分类
     *
     * @param current 当前分类
     * @param all     所有类类
     * @return 包装当前分类
     */
    private CategoryEntity getWithChildren(CategoryEntity current, List<CategoryEntity> all) {
        List<CategoryEntity> categoryEntities = all.stream()
                .filter(category -> Objects.equals(category.getParentCid(), current.getCatId()))
                .map(categoryEntity -> getWithChildren(categoryEntity, all))
                .sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort())))
                .collect(Collectors.toList());
        current.setChildren(categoryEntities);
        return current;
    }

}