package com.mac.scp.service;

import com.mac.common.cbb.MiniuiUtil;
import com.mac.common.cbb.RestResult;
import com.mac.common.domain.TableQueryBean;
import com.mac.common.util.PasswordUtil;
import com.mac.scp.api.IUserService;

import com.mac.scp.domain.*;
import com.mac.scp.mapper.FiletransferMapper;
import com.mac.scp.mapper.UserMapper;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService implements IUserService {
    //private static final Logger log= Logger.getLogger(EssayService.class);
    @Resource
    UserMapper userMapper;

    @Resource
    FiletransferMapper filetransferMapper;

    /**
     * 用户注册
     */
    @Override
    public RestResult<String> registerUser(UserBean userBean) {
        RestResult<String> restResult = new RestResult<String>();
        if (userBean.getTelephone() == null || "".equals(userBean.getTelephone())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名不能为空！");
            return restResult;
        }
        if (userBean.getPassword() == null || "".equals(userBean.getPassword())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("密码不能为空！");
            return restResult;
        }
        if (userBean.getPasswordAgain() == null || "".equals(userBean.getPasswordAgain())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("确认密码不能为空！");
            return restResult;
        }
        if (userBean.getUsername() == null || "".equals(userBean.getUsername())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名不能为空！");
            return restResult;
        }
        if (isUserNameExit(userBean)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名已存在！");
            return restResult;
        }
        if (!isPhoneFormatRight(userBean.getTelephone())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号格式不正确！");
            return restResult;
        }
        if (isPhoneExit(userBean)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号已存在！");
            return restResult;
        }
        if (!userBean.getPassword().equals(userBean.getPasswordAgain())) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("确认密码不一致！");
            return restResult;
        }

        String salt = PasswordUtil.getSaltValue();
        String newPassword = new SimpleHash("MD5", userBean.getPassword(), salt, 1024).toHex();

        userBean.setSalt(salt);

        userBean.setPassword(newPassword);

        int result = userMapper.insertUser(userBean);
        //UserBean userInsertResult = userMapper.selectUserByUserName(userBean);
        userMapper.insertUserRole(userBean.getUserId(), 2);
        UserImageBean userImageBean = new UserImageBean();
        userImageBean.setImageurl("");
        userImageBean.setUserid(userBean.getUserId());
        filetransferMapper.insertUserImage(userImageBean);
        if (result == 1) {
            restResult.setSuccess(true);
            return restResult;
        } else {
            restResult.setSuccess(false);
            restResult.setErrorCode("100000");
            restResult.setErrorMessage("注册用户失败，请检查输入信息！");
            return restResult;
        }
    }

    /**
     * 添加用户
     */
    @Override
    public RestResult<String> addUser(UserBean userBean) {
        RestResult<String> restResult = new RestResult<String>();

        String salt = PasswordUtil.getSaltValue();
        String newPassword = new SimpleHash("MD5", userBean.getPassword(), salt, 1024).toHex();

        userBean.setSalt(salt);

        userBean.setPassword(newPassword);

        int result = userMapper.insertUser(userBean);
        //UserBean userInsertResult = userMapper.selectUserByUserName(userBean);
        userMapper.insertUserRole(userBean.getUserId(), 2);
        UserImageBean userImageBean = new UserImageBean();
        userImageBean.setImageurl("");
        userImageBean.setUserid(userBean.getUserId());
        filetransferMapper.insertUserImage(userImageBean);
        if (result == 1) {
            restResult.setSuccess(true);
            return restResult;
        } else {
            restResult.setSuccess(false);
            restResult.setErrorCode("100000");
            restResult.setErrorMessage("注册用户失败，请检查输入信息！");
            return restResult;
        }
    }

    /**
     * 检测用户名是否存在
     *
     * @param userBean
     */
    private Boolean isUserNameExit(UserBean userBean) {
        UserBean result = userMapper.selectUserByUserName(userBean);
        if (result != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测手机号是否存在
     *
     * @param userBean
     * @return
     */
    private Boolean isPhoneExit(UserBean userBean) {
        List<UserBean> resultList = userMapper.selectUserByTelephone(userBean);
        if (resultList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean isPhoneFormatRight(String phone){
        String regex = "^1\\d{10}";
        boolean isRight = Pattern.matches(regex, phone);
        return isRight;
    }

    /**
     * 通过用户名获取用户信息
     *
     * @param userName
     * @return
     */
    public UserBean findUserInfoByName(String userName) {
        UserBean userinfo = new UserBean();
        userinfo.setUsername(userName);
        return userMapper.selectUserByUserName(userinfo);
    }

    /**
     * 通过用户名获取用户信息
     *
     * @param userName
     * @return
     */
    public UserBean findUserInfoByNameAndPassword(String userName, String password) {
        UserBean userinfo = new UserBean();
        userinfo.setUsername(userName);
        return userMapper.selectUserByUserName(userinfo);
    }

    /**
     * 用户登录
     */
    @Override
    public UserBean loginUser(UserBean userBean) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        if (userBean.getUsername() == null && userBean.getTelephone() != null) {
            userBean.setUsername(userBean.getTelephone());
        }
        if (userBean.getUsername() != null && userBean.getTelephone() == null) {
            userBean.setTelephone(userBean.getUsername());
        }
        UserBean result = userMapper.selectUser(userBean);

        return result;
    }

    @Override
    public List<UserBean> selectAdminUserList() {
        return userMapper.selectAdminUserList();
    }

    @Override
    public UserBean getUserInfoById(long userId) {
        UserBean userBean = userMapper.selectUserById(userId);

        return userBean;
    }

    @Override
    public UserBean selectUserByopenid(String openid) {
        UserBean userBean = userMapper.selectUserByopenid(openid);
        return userBean;
    }


    /**
     * 修改用户信息
     */
    @Override
    public RestResult<String> updateUserInfo(UserBean userBean) {
        RestResult<String> restResult = new RestResult<String>();
        userMapper.updateUserInfo(userBean);

        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 查询所有的用户
     */
    @Override
    public List<UserBean> selectAllUserList() {
        return userMapper.selectAllUserList();
    }

    @Override
    public List<UserBean> selectUserList(TableQueryBean tableQueryBean) {
        TableQueryBean tablePageQuery = MiniuiUtil.getMiniuiTablePageQuery(tableQueryBean);
        return userMapper.selectUserListByCondition(tablePageQuery);
    }

    @Override
    public List<Role> selectRoleList() {
        //TableQueryBean tablePageQuery = MiniuiUtil.getMiniuiTablePageQuery(tableQueryBean);
        return userMapper.selectRoleList();
    }

    @Override
    public List<Permission> selectPermissionList(TableQueryBean tableQueryBean) {
        TableQueryBean tablePageQuery = MiniuiUtil.getMiniuiTablePageQuery(tableQueryBean);
        return userMapper.selectPermissionListByCondition(tablePageQuery);
    }


    @Override
    public int selectUserCountByCondition(TableQueryBean tableQueryBean) {
        return userMapper.selectUserCountByCondition(tableQueryBean);
    }

    @Override
    public void deleteUserInfo(UserBean userBean) {
        userMapper.deleteUserInfo(userBean);

    }

}