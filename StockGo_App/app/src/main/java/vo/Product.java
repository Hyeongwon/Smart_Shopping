package vo;

import java.io.Serializable;

/**
 * Created by byunhyeongwon on 2017. 9. 12..
 */

public class Product implements Serializable {

    int id;
    String product_name;
    int price;

    public int getId() {
        return id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public int getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
