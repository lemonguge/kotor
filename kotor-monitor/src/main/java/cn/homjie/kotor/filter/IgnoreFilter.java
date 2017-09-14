package cn.homjie.kotor.filter;

import cn.homjie.kotor.enums.RootStatus;
import cn.homjie.kotor.model.TxRootStatus;
import org.springframework.stereotype.Component;

import static cn.homjie.kotor.enums.RootStatus.*;

/**
 * @Class IgnoreFilter
 * @Description Ignore过滤器
 * @Author JieHong
 * @Date 2017年3月11日 下午5:02:35
 */
@Component
public class IgnoreFilter extends AbstractFilter {

	@Override
	protected boolean permit(RootStatus key) {
		return CHECK_OK == key || RETRY_CHECK_OK == key || RETRY_FAILED == key;
	}

	@Override
	protected boolean handle(TxRootStatus txRootStatus) {
		// 不再处理
		return true;
	}

}
