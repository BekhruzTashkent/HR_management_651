package com.pdp.apphrmanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<E> {
    private String message;
    private boolean success;
    private String token;
    private Object object;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ApiResponse(String message, boolean success,String token) {
        this.message = message;
        this.success = success;
        this.token=token;
    }
    public ApiResponse(String message, boolean success,Object object) {
        this.message = message;
        this.success = success;
        this.object=object;
    }

    public static Object errorResponse(List<ErrorData> errors) {
        return null;
    }

    public static Object errorResponse(String message, int i) {
        return null;
    }
}
