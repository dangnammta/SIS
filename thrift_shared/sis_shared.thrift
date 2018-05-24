namespace java crdhn.fsp.thrift.data
namespace cpp CRDHN.FSP.Data

typedef i64 TFID
typedef string TCID
typedef i64 TTIME
enum TFileStatus {
	FS_EMPTY = 0;
	FS_UPLOADING = 1;
	FS_UPLOADED = 2;
	FS_UPLOAD_FAIL = 3;
	FS_DELETED = 4;
}

struct TFileInfo {
	1: required TFID fileId = -1;
	2: required string fileName = "";
	3: required string checksumSHA2 = "";
	4: required string checksumMD5 = "";
	5: required i64 fileSize = -1;
	6: required i32 chunkSize = -1;
	7: required i32 numberOfChunks = -1;
	8: required i32 numberOfUploadedChunks = 0;
	9: required list<string> uploadedChunks;
	10: required i32 uploadCount = 0;
	11: required i32 downloadCount = 0;
	12: required i32 fileStatus = TFileStatus.FS_EMPTY;
	13: required TTIME startUploadingTime = -1;
	14: required TTIME endUploadingTime = -1;
	15: required TFID refFileId = -1;
}

struct TChunkInfo{
	1: required TCID chunkId = "";
	2: required TFID fileId = -1;
	3: required string chunkData = "";
	4: required i32 chunkOrderNumber = -1;
}
