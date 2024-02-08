/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orders.Model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Uchovává data o využití itemu
 * @author hrusk
 */
public class ItemUsage {

    private int id;
    private int itemId;
    private int quantity;
    private BigDecimal pricePerPiece;
    private BigDecimal sum;
    private Date purchaseDate;
    private Date usageDate;
    private int helper;

    // Konstruktor pro založení bez znalosti id
    public ItemUsage(int itemId, int quantity, BigDecimal pricePerPiece, Date purchaseDate, Date usageDate) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.pricePerPiece = pricePerPiece;
        this.sum = pricePerPiece.multiply(new BigDecimal(quantity));
        this.purchaseDate = purchaseDate;
        this.usageDate = usageDate;

    }

    //Konstruktor pro založení, pokud id známe
    public ItemUsage(int id, int itemId, int quantity, BigDecimal pricePerPiece, BigDecimal sum, Date purchaseDate, Date usageDate) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.pricePerPiece = pricePerPiece;
        this.sum = sum;
        this.purchaseDate = purchaseDate;
        this.usageDate = usageDate;
        
    }

    // Gettery a settery
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHelper() {
        return helper;
    }

    public void setHelper(int helper) {
        this.helper = helper;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerPiece() {
        return pricePerPiece;
    }

    public void setPricePerPiece(BigDecimal pricePerPiece) {
        this.pricePerPiece = pricePerPiece;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemUsage itemUsage = (ItemUsage) o;
        return id == itemUsage.id
                && itemId == itemUsage.itemId
                && quantity == itemUsage.quantity
                && helper == itemUsage.helper
                && Objects.equals(pricePerPiece, itemUsage.pricePerPiece)
                && Objects.equals(sum, itemUsage.sum)
                && Objects.equals(purchaseDate, itemUsage.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, quantity, pricePerPiece, sum, purchaseDate, usageDate, helper);
    }
}
