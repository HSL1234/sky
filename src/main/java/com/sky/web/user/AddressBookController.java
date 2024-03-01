package com.sky.web.user;

import com.sky.common.result.Result;
import com.sky.pojo.dto.AddressDTO;
import com.sky.pojo.entity.AddressBook;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "地址簿接口")
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list() {
        List<AddressBook> addressBookList =addressBookService.list();
        return Result.success(addressBookList);
    }

    @PutMapping ("/default")
    @ApiOperation("设置默认地址")
    public Result defaultAddress(@RequestBody AddressDTO addressDTO) {
        System.out.println("id = " + addressDTO.getId());
        addressBookService.defaultAddress(addressDTO.getId());
        return Result.success();
    }

    @GetMapping ("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> searchDefaultAddress() {
        AddressBook addressBook = addressBookService.searchDefaultAddress();
        return Result.success(addressBook);
    }

    @PostMapping
    @ApiOperation("新增地址")
    public Result<Object> addAddress(@RequestBody AddressBook addressBook) {
        addressBookService.addAddress(addressBook);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result<Object> updateAddress(@RequestBody AddressBook addressBook) {
        addressBookService.updateAddress(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> searchAddressById(@PathVariable Long id) {
        AddressBook addressBook =  addressBookService.searchAddressById(id);
        return Result.success(addressBook);
    }
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result<Object> deleteAddressById(@RequestParam Long id) {
        addressBookService.deleteAddressById(id);
        return Result.success();
    }
}