package sample.model;

public class ToDo {
    private Integer id;

    private String title;

    private Integer categoryId;

    private Boolean completed;

    public ToDo(Integer id, String title, Integer categoryId, Boolean completed) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
        this.completed = completed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
