package x7.txtrace;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.core.tx.TxTraceable;
import x7.core.web.ViewEntity;

@RestController
@RequestMapping(TxTraceable.ROOT)
public class TxController {

	@RequestMapping(TxTraceable.CONFIRM + "{key}")
	public ViewEntity confirm(@PathVariable("key") String key){
		try{
			boolean flag = TxTracer.confirm(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
	
	@RequestMapping(TxTraceable.CANCEL + "{key}")
	public ViewEntity cancel(@PathVariable("key") String key){
		
		try{
			boolean flag = TxTracer.cancel(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
	
	
	@RequestMapping(TxTraceable.OK + "{key}")
	public ViewEntity ok(@PathVariable("key") String key){
		
		try{
			boolean flag = TxTracer.ok(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
	
	@RequestMapping(TxTraceable.FAIL + "{key}")
	public ViewEntity fail(@PathVariable("key") String key){
		
		try{
			boolean flag = TxTracer.fail(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
}
