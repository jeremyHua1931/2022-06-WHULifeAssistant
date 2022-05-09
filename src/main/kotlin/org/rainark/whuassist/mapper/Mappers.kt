package org.rainark.whuassist.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.rainark.whuassist.entity.User
import org.springframework.stereotype.Component

@Component
interface UserMapper : BaseMapper<User>