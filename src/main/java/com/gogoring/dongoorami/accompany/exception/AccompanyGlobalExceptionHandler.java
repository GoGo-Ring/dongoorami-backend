package com.gogoring.dongoorami.accompany.exception;

import com.gogoring.dongoorami.global.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AccompanyGlobalExceptionHandler {

    @ExceptionHandler(AccompanyPostNotFoundException.class)
    public ResponseEntity<ErrorResponse> catchAccompanyNotFoundException(
            AccompanyPostNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(OnlyWriterCanModifyException.class)
    public ResponseEntity<ErrorResponse> catchOnlyWriterCanModifyException(
            OnlyWriterCanModifyException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidAccompanyPurposeTypeException.class)
    public ResponseEntity<ErrorResponse> catchInvalidAccompanyPurposeTypeExceptionException(
            InvalidAccompanyPurposeTypeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidAccompanyRegionTypeException.class)
    public ResponseEntity<ErrorResponse> catchInvalidRegionTypeException(
            InvalidAccompanyRegionTypeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidAgeRangeException.class)
    public ResponseEntity<ErrorResponse> catchInvalidAgeRangeException(
            InvalidAgeRangeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(IncompleteAgeException.class)
    public ResponseEntity<ErrorResponse> catchIncompleteAgeException(
            IncompleteAgeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AccompanyPostCommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> catchAccompanyPostCommentNotFoundException(
            AccompanyPostCommentNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(DuplicatedAccompanyApplyException.class)
    public ResponseEntity<ErrorResponse> catchDuplicatedAccompanyApplyException(
            DuplicatedAccompanyApplyException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AccompanyApplyCommentModifyDeniedException.class)
    public ResponseEntity<ErrorResponse> catchAccompanyApplyCommentModifyDeniedException(
            AccompanyApplyCommentModifyDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AccompanyApplyNotAllowedForWriterException.class)
    public ResponseEntity<ErrorResponse> catchAccompanyApplyNotAllowedForWriterException(
            AccompanyApplyNotAllowedForWriterException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AlreadyEndedConcertException.class)
    public ResponseEntity<ErrorResponse> catchAlreadyEndedConcertException(
            AlreadyEndedConcertException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AccompanyCommentApplyConfirmDeniedException.class)
    public ResponseEntity<ErrorResponse> catchAccompanyCommentApplyConfirmDeniedException(
            AccompanyCommentApplyConfirmDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AlreadyApplyConfirmedAccompanyCommentException.class)
    public ResponseEntity<ErrorResponse> catchAlreadyApplyConfirmedAccompanyCommentException(
            AlreadyApplyConfirmedAccompanyCommentException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(OnlyWriterCanConfirmApplyException.class)
    public ResponseEntity<ErrorResponse> catchOnlyWriterCanConfirmApplyException(
            OnlyWriterCanConfirmApplyException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidRatingItemTypeException.class)
    public ResponseEntity<ErrorResponse> catchInvalidRatingItemTypeException(
            InvalidRatingItemTypeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
}
