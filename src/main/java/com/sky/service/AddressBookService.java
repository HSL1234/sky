package com.sky.service;

import com.sky.pojo.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list();

    void defaultAddress(Long id);

    AddressBook searchDefaultAddress();

    void addAddress(AddressBook addressBook);

    AddressBook searchAddressById(Long id);

    void updateAddress(AddressBook addressBook);

    void deleteAddressById(Long id);
}
