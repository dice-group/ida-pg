package upb.ida.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import upb.ida.bean.ResponseBean;

public class RespBeanSerializer extends StdSerializer<ResponseBean> {

	public RespBeanSerializer() {
		this(null);
	}

	public RespBeanSerializer(Class<ResponseBean> t) {
		super(t);
	}

	@Override
	public void serialize(ResponseBean value, JsonGenerator jgen, SerializerProvider arg2)
			throws IOException, JsonGenerationException {
		jgen.writeStartObject();
		jgen.writeNumberField("errCode", value.getErrCode());
		jgen.writeStringField("errMsg", value.getErrMsg());
		jgen.writeObjectField("payload", value.getPayload());
		jgen.writeEndObject();
	}

}
