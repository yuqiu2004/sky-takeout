package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Resource
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        AddressBook addressBook = AddressBook.builder().userId(BaseContext.getCurrentId()).build();
        return addressBookMapper.list(addressBook);
    }

    @Override
    public AddressBook getDefault() {
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1)
                .build();
        return addressBookMapper.selectAddressBook(addressBook);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    public void setDefault(AddressBook addressBook) {
        // 先将现在的default设置为非默认
        AddressBook defaultAddress = getDefault();
        if(defaultAddress != null){
            defaultAddress.setIsDefault(0);
            addressBookMapper.update(defaultAddress);
        }
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}
