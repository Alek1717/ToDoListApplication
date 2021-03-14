package sample;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.model.Category;
import sample.model.ToDo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Api {
    private static final String HOST = "http://127.0.0.1";
    private static final String PORT = "8080";
    private static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static Api instance;

    private final OkHttpClient okHttpClient;

    private Api() {
        okHttpClient = new OkHttpClient.Builder().build();
    }

    public static void init() {
        if (instance == null) {
            instance = new Api();
        }
    }

    public static Api getInstance() {
        return instance;
    }

    public List<Category> getCategories() {
        String url = HOST + ":" + PORT + "/category/all";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        List<Category> categoriesList = new ArrayList<>();
        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONArray categories = new JSONArray(Objects.requireNonNull(response.body()).string());
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                int id = category.getInt("id");
                String title = category.getString("title");
                categoriesList.add(new Category(id, title));
            }
            return categoriesList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ToDo> getToDos() {
        String url = HOST + ":" + PORT + "/todoitem/all";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        List<ToDo> toDoList = new ArrayList<>();
        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONArray todoitems = new JSONArray(Objects.requireNonNull(response.body()).string());
            for (int i = 0; i < todoitems.length(); i++) {
                JSONObject todoitem = todoitems.getJSONObject(i);
                int id = todoitem.getInt("id");
                String title = todoitem.getString("title");
                int categoryId = todoitem.getInt("categoryId");
                boolean completed = todoitem.getBoolean("completed");
                toDoList.add(new ToDo(id, title, categoryId, completed));
            }
            return toDoList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int addCategory(String title) {
        String url = HOST + ":" + PORT + "/category/add?title=" + title;
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", TYPE_JSON))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return Integer.parseInt(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int addToDo(String title, int categoryId) {
        String url = HOST + ":" + PORT + "/todoitem/add?title=" + title + "&categoryId=" + categoryId;
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", TYPE_JSON))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return Integer.parseInt(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void updateCategory(int id, String title) {
        String url = HOST + ":" + PORT + "/category/update/" + id + "?title=" + title;
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", TYPE_JSON))
                .build();

        try {
            okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateToDo(int id, String title, boolean completed) {
        String url = HOST + ":" + PORT + "/todoitem/update/" + id + "?title=" + title + "&completed=" + completed;
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", TYPE_JSON))
                .build();

        try {
            okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
