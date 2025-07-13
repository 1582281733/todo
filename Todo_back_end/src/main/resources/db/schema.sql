-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `birthday` varchar(20) DEFAULT NULL COMMENT '生日',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建待办事项表
CREATE TABLE IF NOT EXISTS `todo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '待办名称',
  `type` varchar(50) NOT NULL COMMENT '待办类型',
  `timer_mode` varchar(20) NOT NULL COMMENT '计时方式',
  `duration` int(11) NOT NULL COMMENT '时长(分钟)',
  `background_color` varchar(20) DEFAULT NULL COMMENT '背景颜色',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` varchar(20) DEFAULT 'uncompleted' COMMENT '状态',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

-- 创建计时记录表
CREATE TABLE IF NOT EXISTS `timer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `todo_id` bigint(20) NOT NULL COMMENT '待办事项ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `expected_duration` int(11) NOT NULL COMMENT '预计时长(分钟)',
  `actual_duration` int(11) DEFAULT NULL COMMENT '实际时长(分钟)',
  `completed` tinyint(1) DEFAULT NULL COMMENT '是否完成',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_todo_id` (`todo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计时记录表';

-- 创建系统设置表
CREATE TABLE IF NOT EXISTS `setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `default_rest_time` int(11) DEFAULT '5' COMMENT '默认休息时间(分钟)',
  `theme` varchar(20) DEFAULT 'light' COMMENT '主题',
  `notification_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用通知',
  `sound_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用声音',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 创建名言警句表
CREATE TABLE IF NOT EXISTS `quote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(500) NOT NULL COMMENT '内容',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='名言警句表';

-- 插入一些示例名言警句数据
INSERT INTO `quote` (`content`, `author`) VALUES
('行动是治愈恐惧的良药，而犹豫、拖延将不断滋养恐惧。', '罗宾·夏玛'),
('集中精力做好一件事，胜过用相同的时间和精力做好几件事。', '加里·凯勒'),
('不要等待完美的时刻，抓住现在的时刻，让它变得完美。', '未知'),
('成功不是将来才有的，而是从决定去做的那一刻起，持续累积而成。', '佚名'),
('专注是一种能力，更是一种选择。', '未知'),
('今天的努力，是幸福的伏笔。', '佚名'),
('坚持做自己懒得做但正确的事情，这就是意志力。', '未知'),
('不要高估一天能完成的事，不要低估一年能完成的事。', '未知'),
('每一个不曾起舞的日子，都是对生命的辜负。', '尼采'),
('种一棵树最好的时间是十年前，其次是现在。', '中国谚语'); 