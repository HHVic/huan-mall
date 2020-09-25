package cn.huan.common.constant;

public class WareConstant {

    public enum PurchaseStatus{
        /**
         * 新建
         * 已分配
         * 已领取
         * 已完成
         * 有异常
         */
        NEW(0,"新建"),
        DISTRIBUTION(1,"已分配"),
        RECEIVE(2,"已领取"),
        FINISHED(3,"已完成"),
        ERROR(4,"有异常"),
        ;
        private int code;
        private String value;

        PurchaseStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PurchaseDetailStatus{
        /**
         * 新建
         * 已分配
         * 正在采购
         * 已完成
         * 采购失败
         */
        NEW(0,"新建"),
        DISTRIBUTION(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISHED(3,"已完成"),
        ERROR(4,"采购失败"),
        ;
        private int code;
        private String value;

        PurchaseDetailStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
