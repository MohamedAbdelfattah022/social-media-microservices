export type FileUploadResponse = {
  id: string;
  originalFilename: string;
  storedFilename: string;
  fileSize: number;
  contentType: string;
  uploadedAt: string;
  message: string;
};
