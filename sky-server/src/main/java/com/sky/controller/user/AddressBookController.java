package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api("地址簿管理")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 增加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("增加地址")
    public Result add(@RequestBody AddressBook addressBook){
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 查询当前用户的所有地址
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询当前用户的所有地址")
    public Result list(){
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result defaultAddress(){
        AddressBook addressBook = addressBookService.getDefault();
        return Result.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook addressBook){
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result delete(@RequestParam Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result getInfo(@PathVariable Long id){
        AddressBook addressBook =  addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook);
        return Result.success();
    }
}
