package cn.homjie.kotor.dao;

import cn.homjie.kotor.model.TxTaskInfo;
import cn.homjie.kotor.model.TxTaskInfoWithBLOBs;

import java.util.List;

/**
 * @Class TxTaskInfoDao
 * @Description 事务任务信息DAO
 * @Author JieHong
 * @Date 2017年3月11日 下午4:59:25
 */
public interface TxTaskInfoDao {
	int deleteByPrimaryKey(String id);

	int insert(TxTaskInfoWithBLOBs record);

	int insertSelective(TxTaskInfoWithBLOBs record);

	TxTaskInfoWithBLOBs selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(TxTaskInfoWithBLOBs record);

	int updateByPrimaryKeyWithBLOBs(TxTaskInfoWithBLOBs record);

	int updateByPrimaryKey(TxTaskInfo record);

	List<TxTaskInfoWithBLOBs> selectByDescriptionId(String descriptionId);
}