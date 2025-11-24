package tw.com.ispan.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "screens")
public class ScreenBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "screen_type")
    private String screenType;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Override
    public String toString() {
        return "ScreenBean [id=" + id + ", name=" + name +
                ", totalSeats=" + totalSeats + ", screenType=" + screenType +
                ", price=" + price + "]";
    }

    // Getter / Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public String getScreenType() { return screenType; }
    public void setScreenType(String screenType) { this.screenType = screenType; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}
