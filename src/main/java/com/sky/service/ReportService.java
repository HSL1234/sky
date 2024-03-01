package com.sky.service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

public interface ReportService {
    Integer getOrderCount(LocalDateTime begin, Object o, Integer status);

    void export(HttpServletResponse response);
}
