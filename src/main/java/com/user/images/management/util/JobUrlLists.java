package com.user.images.management.util;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class JobUrlLists {
	
    String id;
    String created;
    String finished;
    String status;
    private final CopyOnWriteArrayList<String> pending;
    private final CopyOnWriteArrayList<String> completed;
    private final CopyOnWriteArrayList<String> failed;

    public JobUrlLists() {
        completed = new CopyOnWriteArrayList<>();
        pending = new CopyOnWriteArrayList<>();
        failed = new CopyOnWriteArrayList<>();
    }

}
