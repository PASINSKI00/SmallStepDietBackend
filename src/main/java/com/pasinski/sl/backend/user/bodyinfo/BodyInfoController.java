package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoResponseForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user/bodyinfo")
public class BodyInfoController {
    private BodyInfoService bodyInfoService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBodyInfo() {
        BodyInfoResponseForm bodyInfoResponseForm;
        try {
            BodyInfo bodyInfo = bodyInfoService.getBodyInfo();
            bodyInfoResponseForm = new BodyInfoResponseForm(bodyInfo);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(bodyInfoResponseForm, HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addBodyInfo(@Valid @RequestBody BodyInfoForm bodyInfoForm) {
        try {
            this.bodyInfoService.addBodyInfo(bodyInfoForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
