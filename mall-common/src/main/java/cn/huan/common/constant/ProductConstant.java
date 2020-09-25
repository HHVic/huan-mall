package cn.huan.common.constant;

public class ProductConstant {
    public enum AttrType {
        ATTR_BASE_TYPE(1,"基本属性"),
        ATTR_SALE_TYPE(0,"销售属性");
        private int code;
        private String value;

        AttrType(int code, String value) {
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

    public enum Image{
        DEFAULT_IMG(1),
        NOT_DEFAULT_IMG(0);
        private int code;

        Image(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PublishStatus{
        NEW(0,"新建"),
        UP(1,"上架"),
        DOWN(2,"下架"),
        ;
        private int code;
        private String value;

        PublishStatus(int code, String value) {
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
