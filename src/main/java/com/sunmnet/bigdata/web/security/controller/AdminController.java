package com.sunmnet.bigdata.web.security.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.security.userdetails.UserDetails;
import com.sunmnet.bigdata.web.security.model.dto.RoleRes;
import com.sunmnet.bigdata.web.security.model.dto.UserQuery;
import com.sunmnet.bigdata.web.security.model.po.SecMenu;
import com.sunmnet.bigdata.web.security.model.po.SecRole;
import com.sunmnet.bigdata.web.security.model.po.SecUser;
import com.sunmnet.bigdata.web.security.service.AdminService;
import com.sunmnet.bigdata.web.security.service.MenuService;
import com.sunmnet.bigdata.web.security.service.SecRoleService;
import com.sunmnet.bigdata.web.security.service.SecUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Value("${security.default-user-password}")
    private String defaultUserPassword;

    @Autowired
    private AdminService adminService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private SecUserService userService;

    @Autowired
    private SecRoleService roleService;

    @PostMapping(value = "/user/save")
    public Object saveUser(@Validated @ModelAttribute SecUser user, BindingResult bindingResult,
                           @RequestParam(name = "roleId", required = false) Integer roleId) {
        if (bindingResult.hasErrors()) {
            return buildErrJson(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (roleId == null || roleId <= 0) {
            return buildErrJson("用户角色错误");
        }
        // 增加同一账户类型的学号或者教工号唯一性校验
        if(! userService.accountUnique(user.getAccountType(), user.getAccountCode())) {
            // 0：未知、1：学生、2：老师
            String accountType = user.getAccountType() == 1 ? "学生" : "教师";
            String accountCodeType = user.getAccountType() == 1 ? "学号" : "教工号";
        	throw new ServiceException(String.format("该%S对应的%S已存在", accountType, accountCodeType));
        }
        user.setUserPassword(defaultUserPassword);
        userService.save(user, roleId);

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUserName());
        data.put("password", defaultUserPassword);
        return buildSuccJson(data);
    }

    @PostMapping(value = "/user/update")
    public Object updateUser(@Validated @ModelAttribute SecUser user, BindingResult bindingResult,
                             @RequestParam(name = "roleId", required = false) Integer roleId) {
        if (bindingResult.hasErrors()) {
            return buildErrJson(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (roleId == null || roleId <= 0) {
            return buildErrJson("用户角色错误");
        }

        if (user.getId() == null || user.getId() <= 0) {
            return buildErrJson("用户ID错误");
        }

        userService.update(user, roleId);
        return buildSuccJson();
    }

    @PostMapping(value = "/user/delete")
    public Object deleteUser(@RequestParam(name = "id", required = false) Integer id) {
        if (id == null || id <= 0) {
            return buildErrJson("用户ID错误");
        }

        userService.delete(id);
        return buildSuccJson();
    }

    @PostMapping(value = "/user/initPwd")
    public Object initUserPwd(@RequestParam(name = "id", required = false) Integer id) {
        if (id == null || id <= 0) {
            return buildErrJson("用户ID错误");
        }
        return buildSuccJson(userService.initPwd(id));
    }

    @GetMapping(value = "/user/list")
    public Object getUserList(@RequestParam(value = "pageNum", defaultValue = "1") String pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") String pageSize,
                              @ModelAttribute UserQuery query) {
        return buildSuccJson(userService.getByPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize), query));
    }
    @GetMapping(value = "/user/listAll")
    public Object getUserList() {
        return buildSuccJson(userService.getAllUser());
    }

    @GetMapping(value = "/user/role/list")
    public Object getUserRoleList() {
        return buildSuccJson(adminService.getAllUserRole());
    }
    
    @RequestMapping(value = "/user/modifyPassword", method = {RequestMethod.POST, RequestMethod.GET})
    public Object modifyPassword(String oldpassword, String newPassword)
    {
    	UserDetails userDetails = authenticationHolder.getAuthenticatedUser();
    	userService.modifyPassword(userDetails, oldpassword, newPassword);
    	
        return buildSuccJson("修改用户密码成功！");
    }

    @PostMapping(value = "/role/save")
    public Object saveRole(@Validated @ModelAttribute SecRole role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildErrJson(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        SecRole sameRole = roleService.getByName(role.getRoleName());
        if (sameRole != null) {
            return buildErrJson("角色名称已经存在");
        }

        roleService.save(role);
        return buildSuccJson();
    }

    @PostMapping(value = "/role/update")
    public Object updateRole(@Validated @ModelAttribute SecRole role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildErrJson(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        if (role.getId() == null || role.getId() <= 0) {
            return buildErrJson("角色ID错误");
        }

        SecRole sameRole = roleService.getByName(role.getRoleName());
        if (sameRole != null && !role.getId().equals(sameRole.getId())) {
            return buildErrJson("角色名称已经存在");
        }

        roleService.update(role);
        return buildSuccJson();
    }

    @PostMapping(value = "/role/delete")
    public Object deleteRole(@RequestParam(name = "id", required = false) Integer id) {
        if (id == null || id <= 0) {
            return buildErrJson("角色ID错误");
        }

        roleService.delete(id);
        return buildSuccJson();
    }

    @GetMapping(value = "/role/list")
    public Object getRoleList() {
        return buildSuccJson(roleService.getAll());
    }

    @GetMapping(value = "/role/res/list")
    public Object getRoleRes(@RequestParam(name = "roleId", required = false) Integer roleId) {
        if (roleId == null || roleId <= 0) {
            return buildErrJson("角色ID必须大于零");
        }
        return buildSuccJson(adminService.getRoleRes(roleId));
    }

    @PostMapping(value = "/role/res/update")
    public Object updateRoleRes(@RequestParam(name = "roleId", required = false) Integer roleId,
                                @RequestParam(name = "menuIds", required = false) String menuIds) {
        if (roleId == null || roleId <= 0) {
            return buildErrJson("角色ID必须大于零");
        }

        RoleRes roleRes = new RoleRes();
        roleRes.setRoleId(roleId);
        roleRes.setMenu(StringUtils.isEmpty(menuIds) ? null :
                Arrays.stream(menuIds.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        adminService.updateRoleRes(roleRes);
        return buildSuccJson();
    }

    @GetMapping(value = "/menu/list")
    public Object getMenuList() {
        return buildSuccJson(menuService.getAllMenuTree());
    }

    @GetMapping(value = "/menu/parent/list")
    public Object getParentMenuList() {
        return buildSuccJson(menuService.getAllParentMenuTree());
    }

    @PostMapping(value = "/menu/save")
    public Object saveMenu(@ModelAttribute SecMenu menu) {
        if (menu == null) {
            return buildErrJson("菜单信息不能为空");
        }
        if (StringUtils.isEmpty(menu.getName())) {
            return buildErrJson("菜单名称不能为空");
        }

        menuService.save(menu);
        return buildSuccJson();
    }

    @PostMapping(value = "/menu/board/save")
    public Object saveBoardMenu(@ModelAttribute SecMenu menu,
                                @RequestParam(name = "boardId", required = false) Integer boardId) {
        if (menu == null) {
            return buildErrJson("菜单信息不能为空");
        }
        if (StringUtils.isEmpty(menu.getName())) {
            return buildErrJson("菜单名称不能为空");
        }
        if (menu.getParentId() == null) {
            return buildErrJson("父菜单不能为空");
        }
        if (StringUtils.isEmpty(menu.getRemark())) {
            return buildErrJson("菜单备注不能为空");
        }
        if (boardId == null || boardId <= 0) {
            return buildErrJson("面板ID必须大于零");
        }

        menu.setUrl("/bigdata/board/show/" + boardId);
        menu.setType(SecMenu.Type.BOARD.getValue());
        menuService.save(menu);
        return buildSuccJson();
    }

    @PostMapping(value = "/menu/update")
    public Object updateMenu(@ModelAttribute SecMenu menu) {
        if (menu == null) {
            return buildErrJson("菜单信息不能为空");
        }
        if (menu.getId() == null) {
            return buildErrJson("菜单ID不能为空");
        }
        if (StringUtils.isEmpty(menu.getName())) {
            return buildErrJson("菜单名称不能为空");
        }

        menuService.update(menu);
        return buildSuccJson();
    }

    @PostMapping(value = "/menu/delete")
    public Object deleteMenu(@RequestParam(name = "id", required = false) Integer id) {
        if (id == null || id <= 0) {
            return buildErrJson("菜单ID错误");
        }

        menuService.delete(id);
        return buildSuccJson();
    }

    @PostMapping(value = "/menu/move")
    public Object moveMenu(@RequestParam(name = "id", required = false) Integer id,
                           @RequestParam(name = "targetSeq", required = false) Integer targetSeq,
                           @RequestParam(name = "targetParentId", required = false) Integer targetParentId) {
        if (id == null || id <= 0) {
            return buildErrJson("菜单ID错误");
        }
        if (targetSeq == null || targetSeq <= 0) {
            return buildErrJson("菜单移动目标序号错误");
        }
        if (targetParentId == null) {
            return buildErrJson("菜单移动目标父ID错误");
        }

        menuService.move(id, targetSeq, targetParentId);
        return buildSuccJson();
    }
}
