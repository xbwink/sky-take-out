package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据分类id查询菜品
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> getByCategoryId(Dish dish);

    /**
     * 向菜品表插入数据
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询
     *
     * @param dish
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dish);

    /**
     * 根据id查询
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(Long[] ids);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据起售状态查询总数
     * @param status
     * @return
     */
    @Select("select count(id) from dish where status = #{status};")
    Integer getCountByStatus(int status);
}
