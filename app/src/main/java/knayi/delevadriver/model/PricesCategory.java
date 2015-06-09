package knayi.delevadriver.model;

/**
 * Created by heinhtetaung on 3/30/15.
 */
public class PricesCategory {

    private String id;
    private String desc;
    private String price;

    public PricesCategory(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
