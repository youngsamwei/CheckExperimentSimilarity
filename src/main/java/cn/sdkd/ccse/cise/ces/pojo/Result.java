package cn.sdkd.ccse.cise.ces.pojo;
/**
 * 实体类：记录上传结果和相关信息
 * @author jaybill
 *
 */
public class Result {
	private byte code;
	private String data;
	public byte getCode() {
		return code;
	}
	public void setCode(byte code) {
		this.code = code;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Result [code=" + code + ", data=" + data + "]";
	}
	
	public Result(){}
	public Result(String data,byte code){
		this.data = data;
		this.code = code;
	}
}
