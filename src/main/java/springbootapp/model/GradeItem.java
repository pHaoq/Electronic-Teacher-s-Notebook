package springbootapp.model;

public class GradeItem {
    private int itemId;
    private String itemName;
    private String grade;

    public GradeItem(int itemId, String itemName, String grade) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.grade = grade;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "GradeItem{itemId=" + itemId + ", itemName='" + itemName + "', grade='" + grade + "'}";
    }
}
