package gr.scify.icsee.data;

import com.google.gson.JsonElement;

import java.util.List;

public class RequestASAPAAnswer {
    int code;
    int count;
    String error;
    List<JsonElement> items;
    String message;
    String more;
    int page;
    int pageSize;
    int total;

    @Override
    public String toString() {
        return "RequestASAPAAnswer{" +
                "code=" + code +
                ", count=" + count +
                ", error='" + error + '\'' +
                ", items=" + items +
                ", message='" + message + '\'' +
                ", more='" + more + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", total=" + total +
                '}';
    }
}

