package com.h2h.springboot_elasticsearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {

    @Select("SELECT df.user_id userId,dui.user_id friendId,dui.headimg friendHeadimg,dui.user_name friendName,du.user_phone friendPhone,df.friend_nickname friendNickname FROM `ddbes_friend` df LEFT JOIN ddbes_user_info dui on df.friend_user_id=dui.user_id LEFT JOIN ddbes_user du on du.user_id=dui.user_id where df.user_id='6f1751cd499d49cd83491950cc0c0aff'")
    List<Map<String,Object>> getUsers();

}
