package com.xn.book.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditAvatarDTO {

    private Integer userId;

    private MultipartFile file;
}
