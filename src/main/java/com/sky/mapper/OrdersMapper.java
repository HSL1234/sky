package com.sky.mapper;

import com.sky.pojo.dto.GoodsSalesDTO;
import com.sky.pojo.entity.Orders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersMapper extends BaseMapper<Orders> {


    @Select("<script>"
            + "select od.name,sum(od.number) number from orders s inner join order_detail od "
            + " where s.status = 5 "
            + "<if test=\"begin != null\"> "
            + " and order_time &gt;= #{begin} "
            + " </if>"
            + " <if test=\"end != null\"> "
            + " and order_time &lt;= #{end} "
            + " </if> "
            + " group by name "
            + " order by number desc "
            + " limit 10 "
            + " </script>")
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
