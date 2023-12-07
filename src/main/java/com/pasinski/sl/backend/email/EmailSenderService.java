package com.pasinski.sl.backend.email;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.email.confirmationToken.EmailConfirmationToken;
import com.pasinski.sl.backend.user.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class EmailSenderService {

    @Async
//    TODO: Before activating validate that OnDelete.Cascade doesn't delete user
    public void emailAddressVerification(AppUser appUser, EmailConfirmationToken token) {
        Email email = new Email();

        email.addRecipient(appUser.getName(), appUser.getEmail());
        email.setTemplateId("zr6ke4n9o1mlon12");

        email.addPersonalization("name", appUser.getName());
        email.addPersonalization("link", ApplicationConstants.EMAIL_CONFIRMATION_URL + token.getToken());

        MailerSend ms = new MailerSend();
        ms.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNzZjYzg1MzZhNjI4YjJkNGEzMWI2NDNiYWU2ODliZWEyNzkyODRmMGFlM2VjZmQyZDc3MzQ0ZDkxMjhlYTk3MTM2MzU4ZTM5MTZkOWYyOTUiLCJpYXQiOjE2Nzg4MjM3ODEuMDQxMDIxLCJuYmYiOjE2Nzg4MjM3ODEuMDQxMDIzLCJleHAiOjQ4MzQ0OTczODEuMDM1MDU4LCJzdWIiOiI2Mjg0MCIsInNjb3BlcyI6WyJlbWFpbF9mdWxsIiwiZG9tYWluc19mdWxsIiwiYWN0aXZpdHlfZnVsbCIsImFuYWx5dGljc19mdWxsIiwidG9rZW5zX2Z1bGwiLCJ3ZWJob29rc19mdWxsIiwidGVtcGxhdGVzX2Z1bGwiLCJzdXBwcmVzc2lvbnNfZnVsbCIsInNtc19mdWxsIiwiZW1haWxfdmVyaWZpY2F0aW9uX2Z1bGwiLCJpbmJvdW5kc19mdWxsIiwicmVjaXBpZW50c19mdWxsIiwic2VuZGVyX2lkZW50aXR5X2Z1bGwiXX0.D9nfK-qTlOScrpFXK5-W0huNw_fJECrucKl1mZyhTyAm6FCE1a7g-5ZNp7E8FgbmCHDaBueOrlPz6v8ySH1DuGkTn4KKuPTbuUfxJzylgb3EF1kiBOxtyhpzPwNgTATGmhEXM2Np64qbDASqPCRRMQ5ZbG9Yt5JTrLBKo5DefNGS-gKZltFJbusDHs3i6giOaLB8IBldk3JQQ-vxro5HCx6jtzoyumuCjqfq2PNZDEvDbZwdTqVmkqdgdBZxtp5G4INV-ksznsS0XgRyJVmxK3Y_U-kgVBU0KFO-oeDRcqudCfFwTNNW_cqTlZ1lIedY5ifgfSasutGPED2hbYoNJFxi8_aZR6DYZK-b7Shm2JXZZUKq5m07rgAr04BFSwoDm5ZrtDRc3Unbgl_mY5l14vGPPPQUGORg0FsbiQBHduFh8s0Sa1cCgjqpmYG-ravGYBRq4sKwSDsaMNJtBXY4MvhVnvjdTuIsuJ7_9IlI3P12A4QIsNhaqVwK2iSCcMViJx8L5aAbiSs8MoNAgR7V1fzK42zQ8Ej7ulXLLM4G-_ig3MovmABzC0ydijKzXEolyP9JYliVwJjd89tIJtRnNNK1HeNZ8dB7h1USbHQ4BRF7DwZ0cgAlgF14hvF4GCM81bGHG_pi_zZeyuh7gGoBN-8BeFZmy9UnyLJjEqGhk3M");

        try {
            MailerSendResponse response = ms.emails().send(email);
            System.out.println(response.messageId);
        } catch (MailerSendException e) {
            e.printStackTrace();
        }
    }
}
