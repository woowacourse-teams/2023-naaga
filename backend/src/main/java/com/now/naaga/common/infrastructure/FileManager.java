package com.now.naaga.common.infrastructure;

import java.nio.file.Path;

public interface FileManager<T> {

    Path save(T t);
}
