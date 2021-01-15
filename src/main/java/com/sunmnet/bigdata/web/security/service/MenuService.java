package com.sunmnet.bigdata.web.security.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.security.model.dto.Menu;
import com.sunmnet.bigdata.web.security.model.po.SecMenu;
import com.sunmnet.bigdata.web.security.persistent.SecMenuDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private SecMenuDao menuDao;

    @Autowired
    private SecResService resService;

    public void save(SecMenu menu) {
        Preconditions.checkNotNull(menu, "菜单信息不能为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(menu.getName()), "菜单名称不能为空");

        if (menu.getIconUrl() == null) {
            menu.setIconUrl("");
        }
        if (menu.getIconSelectedUrl() == null) {
            menu.setIconSelectedUrl("");
        }
        if (menu.getSeq() == null) {
            menu.setSeq(menuDao.selectMaxSeq() + 1);
        }
        menuDao.insertSelective(menu);
    }

    public void update(SecMenu menu) {
        Preconditions.checkNotNull(menu, "菜单信息不能为空");
        Preconditions.checkArgument(menu.getId() != null && menu.getId() > 0, "菜单ID必须大于零");
        Preconditions.checkArgument(StringUtils.isNotEmpty(menu.getName()), "菜单名称不能为空");
        menuDao.updateByIdSelective(menu);
    }

    @Transactional("securityTxManager")
    public void delete(Integer id) {
        Preconditions.checkArgument(id != null && id > 0, "菜单ID必须大于零");
        menuDao.disableMenuById(id);
        resService.deleteByMenuId(id);
    }

    @Transactional("securityTxManager")
    public void move(Integer id, Integer targetSeq, Integer targetParentId) {
        Preconditions.checkArgument(id != null && id > 0, "菜单ID必须大于零");
        Preconditions.checkArgument(targetSeq != null && targetSeq > 0, "菜单移动目标序号必须大于零");
        Preconditions.checkArgument(targetParentId != null, "菜单移动目标父ID不能为空");

        SecMenu menu = new SecMenu();
        menu.setId(id);
        menu.setSeq(targetSeq);
        menu.setParentId(targetParentId);

        menuDao.increSeq(targetSeq);
        menuDao.updateByIdSelective(menu);
    }

    public List<SecMenu> getAllMenu() {
        List<SecMenu> menus = menuDao.selectAll();
        if (menus == null) {
            menus = Collections.emptyList();
        }
        return menus;
    }

    public List<Menu> getAllMenuTree() {
        return parseToMenuDTOList(menuDao.selectAll());
    }

    public List<Menu> getAllParentMenuTree() {
        List<SecMenu> menus = getAllMenu();
        return parseToMenuDTOList(menus.stream().filter(i -> StringUtils.isEmpty(i.getUrl()))
                .collect(Collectors.toList()));
    }

    public List<Menu> getAllAuthorizedMenuTreeOfUser(Integer userId) {
        Preconditions.checkArgument(userId != null && userId > 0);
        return parseToMenuDTOList(menuDao.selectAllAuthorizedMenuOfUser(userId));
    }

    // 将所有菜单转化为相应DTO对象，并且初始化各个菜单子菜单
//    private List<Menu> parseToMenuDTOList(List<SecMenu> menus) {
//        if (CollectionUtils.isEmpty(menus)) {
//            return Collections.emptyList();
//        }
//
//        List<Menu> allMenus = new ArrayList<>();
//        for (SecMenu menu : menus) {
//            allMenus.add(parseToMenuDTO(menu));
//        }
//
//        initSubMenus(allMenus);
//        return findTopLevelMenus(allMenus);
//    }
    // 将所有菜单转化为相应DTO对象，并且初始化各个菜单子菜单
    private List<Menu> parseToMenuDTOList(List<SecMenu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        List<Menu> allMenus = new ArrayList<>();
        for (SecMenu menu : menus) {
            allMenus.add(parseToMenuDTO(menu));
        }

//        initSubMenus(allMenus);
        return initSubMenusThree(allMenus);
    }

    // 转化为DTO
    private Menu parseToMenuDTO(SecMenu menu) {
        Menu menuDto = new Menu();
        menuDto.setId(menu.getId());
        menuDto.setParentId(menu.getParentId());
        menuDto.setName(menu.getName());
        menuDto.setUrl(menu.getUrl());
        menuDto.setIconUrl(menu.getIconUrl());
        menuDto.setIconSelectedUrl(menu.getIconSelectedUrl());
        menuDto.setSeq(menu.getSeq());
        menuDto.setTags(StringUtils.isEmpty(menu.getRemark()) ? Collections.emptyList() :
                Lists.newArrayList(menu.getRemark().split(",")));
        return menuDto;
    }

    // 初始化所有菜单的子菜单
    private void initSubMenus(List<Menu> allMenus) {
        for (Menu menu : allMenus) {
            List<Menu> subMenus = new ArrayList<>();
            for (Menu $menu : allMenus) {
                if ($menu.getParentId().equals(menu.getId())) {
                    subMenus.add($menu);
                }
            }

            menu.setSubMenus(subMenus);
        }
    }


	// 初始化所有菜单的子菜单 包括三级
	private List<Menu> initSubMenusThree(List<Menu> allMenus) {
		List<Menu> firstMenuInfoModelList = new ArrayList<>();
		for(Menu menu : allMenus){
			if (-1 == menu.getParentId())
			{
				firstMenuInfoModelList.add(menu);
			}
		}
		//处理第二级别菜单
		for(Menu firstMenuInfoModel : firstMenuInfoModelList) {
			List<Menu> secondMenuInfoModelList = new ArrayList<>();
			for (Menu menu : allMenus) {
				if (firstMenuInfoModel.getId().intValue() == menu.getParentId().intValue()) {
					secondMenuInfoModelList.add(menu);
				}
			}
			//处理第三级别菜单
			for (Menu secondMenu : secondMenuInfoModelList) {
				List<Menu> threeMenuInfoModelList = new ArrayList<>();
				for (Menu menu : allMenus) {
					if (secondMenu.getId().intValue() == menu.getParentId().intValue()) {
						threeMenuInfoModelList.add(menu);
					}
				}
				secondMenu.setSubMenus(threeMenuInfoModelList);
			}
			firstMenuInfoModel.setSubMenus(secondMenuInfoModelList);
		}
		return firstMenuInfoModelList;
	}
    // 获取所有顶级菜单
    private List<Menu> findTopLevelMenus(List<Menu> allMenus) {
        List<Menu> topLevelMenus = new ArrayList<>();
        for (Menu menu : allMenus) {
            boolean hasParentMenu = false;
            for (Menu $menu : allMenus) {
                if ($menu.getId().equals(menu.getParentId())) {
                    hasParentMenu = true;
                }
            }

            if (!hasParentMenu) {
                topLevelMenus.add(menu);
            }
        }
        return topLevelMenus;
    }
}
