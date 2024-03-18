package com.sky.service;


import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.enumeration.OperationType;

public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @AutoFill(OperationType.INSERT)
    void save(SetmealDTO setmealDTO);
}
