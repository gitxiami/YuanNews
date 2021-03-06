package yuan.ssm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import yuan.ssm.common.config.ConfigConstant;
import yuan.ssm.common.constant.ManagerConstant;
import yuan.ssm.common.util.LoggerUtil;
import yuan.ssm.other.PageJo;
import yuan.ssm.service.manager.IndexManager;
import yuan.ssm.service.manager.UserManager;
import yuan.ssm.vo.UserVo;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * Created by yuan on 16-4-4.
 * 　控制器　－　后台管理　
 */

@Controller
@RequestMapping(value = "/manager")
public class UserController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private IndexManager indexManager;

    private int userPageNum= ConfigConstant.MAGAGER_USER_PAGE_NUM;

    /**
     * 管理员登陆　action
     */
    @RequestMapping(value = "/managerLogin",method= RequestMethod.POST)
    public @ResponseBody Integer managerLogin(HttpSession httpSession, @RequestParam String unum, @RequestParam String pass) throws Exception {
        LoggerUtil.print(ManagerConstant.LOG_MANAGER_CONTROLLER,unum,pass);
        Integer uid = userManager.managerLogin(unum, pass);

            if(uid>0){
                httpSession.setAttribute(ManagerConstant.SESSION_ATTRIBUTE_KEY,uid);
            }

        return uid;
    }

    /**
     * 后台主页面
     * @Params 注意这个是后台管理主页
     */
    @RequestMapping(value = "/managerIndex",method = RequestMethod.GET)
    public ModelAndView managerIndex(HttpSession httpSession) throws Exception {
        Integer uid= (Integer) httpSession.getAttribute(ManagerConstant.SESSION_ATTRIBUTE_KEY);
        UserVo userVo = userManager.managerFindUserIfo(uid);
        httpSession.setAttribute(ManagerConstant.MANAGER_USER_NAME,userVo);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName(ManagerConstant.MANAGER_INDEX);

        Integer newsCount = indexManager.getCount(2);//新闻
        Integer commentCount = indexManager.getCount(3);//评论
        Integer userCount = indexManager.getCount(1);//用户
        Integer tasteCount = indexManager.getCount(5);//兴趣
        List<String> logs = indexManager.getLogs();//后台更新日志
        modelAndView.addObject("newsCount",newsCount);
        modelAndView.addObject("commentCount",commentCount);
        modelAndView.addObject("userCount",userCount);
        modelAndView.addObject("tasteCount",tasteCount);
        modelAndView.addObject("logs",logs);
        return modelAndView;
    }

    /**
     * 管理员－登出
     * 登出转向　登陆页面
     */
    @RequestMapping("/managerLoginOut")
    public String managerLoginOut(HttpSession httpSession){
        httpSession.invalidate();
        return ManagerConstant.MANAGER_REDRICT_LOGIN;
    }

    /**
     * 后台管理－用户管理
     */
    @RequestMapping(value = "/managerUserPage",method = RequestMethod.GET)
    public ModelAndView managerUserPage(@RequestParam Integer p) throws Exception {
        if(p==null){
            p=1;
        }
        if(p<0){
            p=1;
        }
        int currentPage=p;
        LoggerUtil.print("用户管理：　p= "+p);
        p=userPageNum*(p-1);
        List<UserVo> userVos = userManager.managerFindUserList(p,userPageNum);
        LoggerUtil.print("用户管理：　userVos= "+userVos);
        PageJo jo = userManager.managerFindUserCount();
        LoggerUtil.print("用户管理：　PageJo = "+jo.getAllCount());
        ModelAndView mav=new ModelAndView();
        mav.addObject("userVos",userVos);
        mav.setViewName(ManagerConstant.MAGAGER_USER_PAGE);
        mav.addObject("currentPage",currentPage);
        mav.addObject("userCount",jo.getAllCount());
        return mav;
    }

    /**
     * 修改用户信息
     * @param userVo　用户vo
     * @return 状态
     * @throws Exception
     */
    @RequestMapping(value = "/managerUpdateUser",method = RequestMethod.POST)
    public @ResponseBody Integer managerUpdateUser(@ModelAttribute UserVo userVo) throws Exception{
        LoggerUtil.print("修改的用户信息　：　"+userVo);
        return userManager.managerUpdateUserIfo(userVo);
    }

    /**
     * 获取单个用户信息
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/managerUserIfo",method = RequestMethod.POST)
    public @ResponseBody UserVo managerUserIfo(@RequestParam Integer id) throws Exception{
        LoggerUtil.print("获取用户的id　：　"+id);
        return userManager.managerFindUserIfo(id);
    }

    /**
     * 删除用户信息
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/managerDeleteUserIfo",method = RequestMethod.POST)
    public @ResponseBody Integer managerDeleteUserIfo(@RequestParam Integer id) throws Exception{
        LoggerUtil.print("删除用户id　：　"+id);
        return userManager.managerDeleteUserIfo(id);
    }

    /**
     * 新添加用户信息
     * @param userVo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/managerInsertUserIfo",method = RequestMethod.POST)
    public @ResponseBody Integer managerInsertUserIfo(@ModelAttribute UserVo userVo) throws Exception{
        LoggerUtil.print("新添加的用户信息　：　"+userVo);
        return userManager.managerInsertUserIfo(userVo);
    }

    /**
     * 恢复用户信息
     * @param id　用户id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/managerSettingUserIfo",method = RequestMethod.POST)
    public @ResponseBody Integer managerSettingUserIfo(@RequestParam Integer id) throws Exception{
        LoggerUtil.print("修改用户的id : "+id);
        return userManager.managerSettingUserIfo(id);
    }

}
