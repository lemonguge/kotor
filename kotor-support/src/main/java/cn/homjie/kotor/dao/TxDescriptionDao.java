package cn.homjie.kotor.dao;


import cn.homjie.kotor.model.TxDescription;

import java.util.List;

/**
 * @Class TxDescriptionDao
 * @Description 事务描述DAO
 * @Author JieHong
 * @Date 2017年3月11日 下午4:58:47
 */
public interface TxDescriptionDao {
	int deleteByPrimaryKey(String id);

	int insert(TxDescription record);

	int insertSelective(TxDescription record);

	TxDescription selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(TxDescription record);

	int updateByPrimaryKeyWithBLOBs(TxDescription record);

	int updateByPrimaryKey(TxDescription record);

	Boolean selectChildReady(String root);

	Boolean selectInfosReady(String root);

	List<TxDescription> selectTotalByRoot(String root);
}