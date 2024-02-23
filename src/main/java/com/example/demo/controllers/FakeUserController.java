package com.example.demo.controllers;

import com.example.demo.auth.services.FakeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Avijeet
 * @project UserRegistration
 * @github avijeetas
 * @date 02-23-2024
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/fake")
public class FakeUserController {
   private final FakeUserService fakeUserService;

   @PostMapping()
   public ResponseEntity<?> generateFake(@RequestParam Integer count){
      fakeUserService.createFakeUsers(count);
      return ResponseEntity.ok("Done");
   }
}
