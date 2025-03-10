DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `id` int(11) NOT NULL,
  `code` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` int(11) NOT NULL,
  `job_no` varchar(45) NOT NULL,
  `name` varchar(100) NOT NULL,
  `age` int(3) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `entry_date` datetime NOT NULL,
  `department_id` int(11) NOT NULL,
  `birthday` date NOT NULL,
  `post` varchar(100) NOT NULL,
  `salary` decimal(8, 2) NOT NULL,
  `address` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);
