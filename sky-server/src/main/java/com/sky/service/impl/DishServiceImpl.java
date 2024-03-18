package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品&对应口味
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // dish表插入1条数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();

        // dish_flavor表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(f -> f.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品是否能删除--是否起售中
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                // 起售中不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 判断当前菜品是否能删除--是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 被套餐关联不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的菜品
        // for (Long id : ids) {
        //     dishMapper.deleteById(id);
        //     // 删除菜品关联的口味
        //     dishFlavorMapper.deleteByDishId(id);
        // }

        // 根据菜品id集合批量删除菜品
        dishMapper.deleteByIds(ids);
        // 根据菜品id集合批量删除口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品&口味
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // dish
        Dish dish = dishMapper.getById(id);
        // flavor
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 关联flavor先全部删除再加上
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        dishFlavorMapper.insertBatch(dishDTO.getFlavors());
    }

}
