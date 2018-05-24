namespace java crdhn.sis.thrift.response
namespace cpp CRDHN.SIS.Response

enum TErrorCode {
	EC_OK = 0;
	EC_SYSTEM = 1;
	EC_PARAM_ERROR = 2;
}

struct TError{
	1: required i32 errorCode = TErrorCode.EC_OK;
	2: optional string errorMessage;
}

struct TISScaleImageResponse{
	1: required TError error;
	2: required string imageHeader = "";
}
