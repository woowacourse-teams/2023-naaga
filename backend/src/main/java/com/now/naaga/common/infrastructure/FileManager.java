package com.now.naaga.common.infrastructure;

import java.io.File;

public interface FileManager<T> {

    File save(T t, String saveDirectory);
}
