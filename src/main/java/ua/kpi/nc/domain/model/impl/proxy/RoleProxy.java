package ua.kpi.nc.domain.model.impl.proxy;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.kpi.nc.config.AppConfig;
import ua.kpi.nc.config.DataConfig;
import ua.kpi.nc.domain.model.Role;
import ua.kpi.nc.domain.model.User;
import ua.kpi.nc.domain.model.impl.real.RoleImpl;
import ua.kpi.nc.service.RoleService;

import java.util.Set;

/**
 * Created by Chalienko on 13.04.2016.
 */
public class RoleProxy implements Role {

    private Long id;
    private RoleImpl role;

    public RoleProxy(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getRoleName() {
        if (role == null) {
            role = downloadRole();
        }
        return role.getRoleName();
    }

    @Override
    public void setRoleName(String roleName) {
        if (role == null) {
            role = downloadRole();
        }
        role.setRoleName(roleName);
    }

    @Override
    public Set<User> getUsers() {
        if (role == null) {
            role = downloadRole();
        }
        return role.getUsers();
    }

    @Override
    public void setUsers(Set<User> users) {
        if (role == null) {
            role = downloadRole();
        }
        role.setUsers(users);
    }

    private RoleImpl downloadRole(){
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.register(DataConfig.class);
        appContext.refresh();
        RoleService roleService = (RoleService) appContext.getBean("roleServiceImpl");
        return (RoleImpl) roleService.getRoleById(id);
    }
}
