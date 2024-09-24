package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 添加地址
     * @param addressBook
     */
    void add(AddressBook addressBook);

    /**
     * 查询当前用户的地址
     * @return
     */
    List<AddressBook> list();

    /**
     * 默认地址查询
     * @return
     */
    AddressBook getDefault();

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 设置默认地址
     * @param id
     */
    void setDefault(AddressBook addressBook);
}
