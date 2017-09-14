package cn.homjie.kotor.dao;

import cn.homjie.kotor.model.TxRootStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Class TxRootStatusDao
 * @Description 事务跟描述状态DAO
 * @Author JieHong
 * @Date 2017年3月11日 下午4:59:04
 */
public interface TxRootStatusDao {
	int deleteByPrimaryKey(String root);

	int insert(TxRootStatus record);

	int insertSelective(TxRootStatus record);

	TxRootStatus selectByPrimaryKey(String root);

	int updateByPrimaryKeySelective(TxRootStatus record);

	int updateByPrimaryKeyWithBLOBs(TxRootStatus record);

	int updateByPrimaryKey(TxRootStatus record);

	List<TxRootStatus> selectBaseByStatusList(@Param("statusList") List<String> statusList);

}