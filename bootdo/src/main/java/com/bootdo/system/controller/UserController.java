package com.bootdo.system.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.common.annotation.Log;
import com.bootdo.common.utils.MD5Utils;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;
import com.bootdo.system.domain.RoleDO;
import com.bootdo.system.domain.SysUserDO;
import com.bootdo.system.service.RoleService;
import com.bootdo.system.service.UserService;

@RequestMapping("/sys/user")
@Controller
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;

	@GetMapping("")
	String user(Model model) {
		return "sys/user/user";
	}

	/**
	 * 获取全部用户列表
	 * 
	 * @param
	 * @return 全部用户对象
	 */
	@GetMapping("/list")
	@ResponseBody
	PageUtils list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<SysUserDO> sysUserList = userService.list(query);
		int total = userService.count(query);
		PageUtils pageUtil = new PageUtils(sysUserList, total);
		return pageUtil;
	}
	@Log("添加用户")
	@GetMapping("/add")
	String add(Model model) {
		List<RoleDO> roles = roleService.list();
		model.addAttribute("roles", roles);
		return "sys/user/add";
	}
	@Log("编辑用户")
	@GetMapping("/edit/{id}")
	String edit(Model model, @PathVariable("id") Long id) {
		SysUserDO userDO = userService.get(id);
		model.addAttribute("user", userDO);
		List<RoleDO> roles = roleService.list(id);
		model.addAttribute("roles", roles);
		return "sys/user/edit";
	}
	@Log("保存用户")
	@PostMapping("/save")
	@ResponseBody
	R save(SysUserDO user) {
		user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
		if (userService.save(user) > 0) {
			return R.ok();
		}
		return R.error();
	}
	@Log("更新用户")
	@PostMapping("/update")
	@ResponseBody
	R update(SysUserDO user) {
		if (userService.update(user) > 0) {
			return R.ok();
		}
		return R.error();
	}
	@Log("删除用户")
	@PostMapping("/remove")
	@ResponseBody
	Map<String, Boolean> remove(Long id) {
		Map<String, Boolean> rMap = new HashMap<>();
		rMap.put("success", userService.remove(id) > 0);
		return rMap;
	}
	@Log("批量删除用户")
	@PostMapping("/batchRemove")
	@ResponseBody
	R  batchRemove(@RequestParam("ids[]") Long[] userIds) {
		
		List<Long> Ids = Arrays.asList(userIds);
		int r = userService.batchremove(Ids);
		System.out.println(r);
		if (r>0) {
			return R.ok();
		};
		return R.error();
	}

	@PostMapping("/exit")
	@ResponseBody
	boolean exit(Map<String, Object> params) {
		return !userService.exit(params);// 存在，不通过，false
	}
	@Log("请求更改用户密码")
	@GetMapping("/resetPwd/{id}")
	String resetPwd(@PathVariable("id") Long userId, Model model) {
		SysUserDO userDO = new SysUserDO();
		userDO.setUserId(userId);
		model.addAttribute("user", userDO);
		return "sys/user/reset_pwd";
	}
	@Log("提交更改用户密码")
	@PostMapping("/resetPwd")
	@ResponseBody
	R resetPwd(SysUserDO user) {
		user.setPassword(MD5Utils.encrypt(userService.get(user.getUserId()).getUsername(), user.getPassword()));
		if (userService.resetPwd(user)>0) {
			return R.ok();
		}
		return R.error();
	}

}
