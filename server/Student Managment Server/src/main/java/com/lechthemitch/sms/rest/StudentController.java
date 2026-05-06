package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dto.StudentDTO;
import com.lechthemitch.sms.dto.StudentResponseDTO;
import com.lechthemitch.sms.service.QrCodeService;
import com.lechthemitch.sms.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;
    private final QrCodeService qrCodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponseDTO create(@Valid @RequestBody StudentDTO dto) {
        return studentService.create(dto);
    }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getStudentQr(@PathVariable Integer id,
                                               @RequestParam(defaultValue = "300") int width,
                                               @RequestParam(defaultValue = "300") int height) {
        StudentResponseDTO student = studentService.findById(id);
        byte[] png = qrCodeService.generateQrCode(student.qrCode(), width, height);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }

    @GetMapping
    public List<StudentResponseDTO> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public StudentResponseDTO findById(@PathVariable Integer id) {
        return studentService.findById(id);
    }

    @GetMapping("/parent/{parentId}")
    public List<StudentResponseDTO> findByParentId(@PathVariable Integer parentId) {
        return studentService.findByParentId(parentId);
    }

    @PutMapping("/{id}")
    public StudentResponseDTO update(@PathVariable Integer id, @Valid @RequestBody StudentDTO dto) {
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        studentService.deleteById(id);
    }
}
