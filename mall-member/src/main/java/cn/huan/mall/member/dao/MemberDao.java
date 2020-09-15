package cn.huan.mall.member.dao;

import cn.huan.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author konghuan
 * @email 714548838@qq.com
 * @date 2020-09-15 01:58:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
