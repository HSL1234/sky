package com.sky.service.impl;

import com.sky.common.context.BaseContext;
import com.sky.mapper.AddressBookMapper;
import com.sky.pojo.entity.AddressBook;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Resource
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> list() {
        AddressBook addressBook = AddressBook.builder().userId(BaseContext.getCurrentId()).build();
        return addressBookMapper.select(addressBook);
    }

    @Override
    public void defaultAddress(Long id) {
        Example example = new Example(AddressBook.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", BaseContext.getCurrentId());
        // 将所有用户ID为当前用户的默认地址设置为false
        addressBookMapper.updateByExampleSelective(AddressBook.builder().isDefault(0).build(), example);
        // 再将要设置的地址is_default设置为true
        addressBookMapper.updateByPrimaryKeySelective(AddressBook.builder().isDefault(1).id(id).build());
    }

    @Override
    public AddressBook searchDefaultAddress() {
        return addressBookMapper.selectOne(AddressBook.builder().isDefault(1).userId(BaseContext.getCurrentId()).build());
    }

    @Override
    public void addAddress(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }

    @Override
    public AddressBook searchAddressById(Long id) {
        return addressBookMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateAddress(AddressBook addressBook) {
    addressBookMapper.updateByPrimaryKeySelective(addressBook);
    }

    @Override
    public void deleteAddressById(Long id) {
        addressBookMapper.deleteByPrimaryKey(id);
    }
}