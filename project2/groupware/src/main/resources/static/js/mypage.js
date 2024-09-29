// - 전화번호 - 형식 포맷
function phoneNumberFormat(phoneNumber) {
  function formatPhoneNumber(phoneNumber) {
        // 숫자만 추출
        let cleaned = ('' + phoneNumber).replace(/\D/g, '');

        // 전화번호 포맷
        if (cleaned.length === 11) {
          return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        }
        return phoneNumber; // 포맷이 잘못된 경우 원본 반환
  }

  // 모든 연락처 항목을 순회하며 포맷 적용
  $('.phone-replace').each(function() {
    let $this = $(this);
    let phoneNumber = $this.text().trim();
    let formattedNumber = formatPhoneNumber(phoneNumber);
    $this.text(formattedNumber);
  });
}


function execDaumPostcode() {
       new daum.Postcode({
           oncomplete: function(data) {
               // 팝업을 통한 검색 결과 항목 클릭 시 실행
               var addr = ''; // 주소_결과값이 없을 경우 공백
               var extraAddr = ''; // 참고항목

               //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
               if (data.userSelectedType === 'R') { // 도로명 주소를 선택
                   addr = data.roadAddress;
               } else { // 지번 주소를 선택
                   addr = data.jibunAddress;
               }

               if(data.userSelectedType === 'R'){
                   if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                       extraAddr += data.bname;
                   }
                   if(data.buildingName !== '' && data.apartment === 'Y'){
                       extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                   }
                   if(extraAddr !== ''){
                       extraAddr = ' (' + extraAddr + ')';
                   }
               } else {
                 $('UserAdd1').val('');
               }
             // 선택된 우편번호와 주소 정보를 input 박스에 넣는다.
               $('#zipp_code_id').val(data.zonecode);
               $('#UserAdd1').val(addr);
               $("#UserAdd1").val(function(index, value) {
                       return value + extraAddr;
               });
               $('#UserAdd2').focus(); // 우편번호 + 주소 입력이 완료되었음으로 상세주소로 포커스 이동
           }
       }).open();
 }

$(function () {
    // ajax.fn
    function commonAjax(url, method, data, dataType="text"){
        return new Promise((resolve, reject) => {
            $.ajax({
                url : url,
                method : method,
                data : data,
                dataType : dataType,
                success : function(result){
                    resolve(result);
                },
                error : function(xhr, status, err) {
                    reject(err);
                }
            });
        });
    };

    function paginationCode(url) {
          // 현재는 페이지가 같아서 선택자로 'load 컨테이너' 안에있는 '.paging'
          let pageNo = $(url).data('pageno');
          const lastPageNo = Math.floor(($(url).data('datacount') / 5) + (($(url).data('datacount') % 5) > 0 ? 1 : 0));
          if (/«/.test($(this).text())) pageNo = 1;
          if ($(this).text() == 'Previous') pageNo = pageNo - 1 < 1 ? 1 : pageNo - 1;
          if ($(this).text() == 'Next') pageNo = pageNo + 1 > lastPageNo ? lastPageNo : pageNo + 1;
          if (/»/.test($(this).text())) pageNo = lastPageNo;
          if (!isNaN($(this).text())) pageNo = $(this).text();
          return pageNo;
    }

    // 마이 페이지 첫 화면
    $('.my-info-div').load('my-information', function(){
          // 휴대폰 번호 형식 안내
          const phoneInput = $('.my-info-div #empPhone');
          phoneInput.on('keydown', function(e) {
                if (e.key === '-') {
                    alert("'-'를 제외한 숫자만 입력해 주세요");
                    e.preventDefault();
                }
          });
    });

    // 데이피커 로드 및 오늘 날짜로 초기값 설정
    function datePickerLoad() {
        $('.datepicker').datepicker({
                format: "yyyy-mm-dd",
                autoclose: true,
                todayHighlight: true,
                lang: 'kr'
        }).datepicker('setDate', new Date()); // 오늘 날짜로 초기값 설정
    }

    $('.my-attendance-btn').on('click', (e) => {
          $('.attendance-list-container').load('my-attendance', function() {
              datePickerLoad();
              $('.attd-search-keyword').val('all');
          });
    });

     $('.my-loginAttempt-btn').on('click', (e) => {
          $('.login-list-container').load('login-attempt', function(){
              datePickerLoad();
              $('.login-search-keyword').val('all');
          });
    });

    $('.my-memo-btn').on('click', (e) => {
          $('.todo-list').load('todo-list');
    });

    $('.my-bookmark-btn').on('click', (e) => {
          $('.bookmark-list-container').load('my-bookmark', function(){
                datePickerLoad();
                phoneNumberFormat();

                $('.bookmark-search-option').val('all');
                $('#bookmark-search-keyword').val('');
          });
    });

    // 즐겨찾기탭 전체선택 or 전체해제 이벤트
    $('.bookmark-list-container').on('change', '#bookmark-all-checkbox', function(e) {
          let chk = $('.bookmark-list-container .book-check');

          if($(this).is(':checked')) {
              chk.prop('checked', true);
          } else {
              chk.prop('checked', false);
          }
    });


    let sortName = '';
    let name = false;
    let dept = false;

    // 즐겨찾기탭 별 표시
    $('.bookmark-list-container').on('click', '.star-check', function(e) {

          let $this = $(this);
          let starCheck = $this.data('star-check');
          let index = $this.data('index');
          let contactId = $('.contact-id').eq(index).text();

          let pageNo = $('.bookmark-list-container .paging').data('pageno');
          let option = $('.bookmark-search-option > option:selected').val();
          let bookmarkKeyword = $('#bookmark-search-keyword').val();

          let value = sortName == 'name' ? name : dept;
          commonAjax('my-bookmark-set', 'POST', { bookmarkActive : starCheck, contactId : contactId })
              .then(result => {
                  $('.bookmark-list-container').load('my-bookmark?pageNo=' + 1 +
                      '&option=' + option +
                      '&keyword=' + bookmarkKeyword +
                      '&sortName=' + sortName +
                      '&sortValue=' + value, function(){
                              phoneNumberFormat();
                      }
                  );
              })
              .catch(err => {
                  alert('실패');
              });

          if(starCheck == true || starCheck == 'true') {
              $(this).attr('data-star-check', 'false');
              $(this).children().removeClass('fas').addClass('far');

          } else {
              $(this).attr('data-star-check', 'true');
              $(this).children().removeClass('far').addClass('fas');
          }
    });

    // 이름 or 부서 정렬 이벤트
    $('.bookmark-list-container').on('click', '.bookmark-sort', function(e) {
          let option = $('.bookmark-search-option > option:selected').val();
          let bookmarkKeyword = $('#bookmark-search-keyword').val();

          sortName = $(this).data('bookmark-sort'); // name or dept
          name = $(this).data('bookmark-name'); // true or false
          dept = $(this).data('bookmark-dept'); // true or false

          if(sortName == 'name')  {
                name = name === true ? false : true
                $(this).data('bookmark-name', name);

                $('.bookmark-list-container').load('my-bookmark?pageNo=' + 1 +
                                              '&option=' + option +
                                              '&keyword=' + bookmarkKeyword +
                                              '&sortName=' + sortName +
                                              '&sortValue=' + name, function(){
                                                      phoneNumberFormat();
                                              }
                );
          }

          if(sortName == 'dept') {
                dept = dept === true ? false : true
                $(this).data('bookmark-dept', dept);

                // ajax code
                $('.bookmark-list-container').load('my-bookmark?pageNo=' + 1 +
                                              '&option=' + option +
                                              '&keyword=' + bookmarkKeyword +
                                              '&sortName=' + sortName +
                                              '&sortValue=' + dept, function(){
                                                      phoneNumberFormat();
                                              }
                );
          }
    });

    // 즐겨찾기 탭에서 삭제
    $('#bookmark-remove-btn').on('click', function(e) {
          e.preventDefault();
          e.stopPropagation();

          let dataToSend = [];
          $('#bookmark-table tbody tr').each(function() {
                let $row = $(this);
                let $checkbox = $row.find('.book-check');

                if($checkbox.is(':checked')) {
                      let contactName = $row.find('td').eq(2).text();
                      let contactId = $row.find('td').eq(3).text();

                      dataToSend.push({
                          empName: contactName,
                          contactId: contactId,
                      });
                }
          });

        if (dataToSend.length > 0) {
            // `empName`을 배열로 수집
            let names = dataToSend.map(item => item.empName);

            let confirmMessage;

            // 1명이면
            if (dataToSend.length === 1) {
                confirmMessage = names[0] + '님을 삭제하시겠습니까?';
            } else {
                // 2명 이상부터
                let namesString = names.join(', ');
                confirmMessage = namesString + '님을 삭제하시겠습니까?';
            }

            if (confirm(confirmMessage)) {
                new Promise((resolve, reject) => {
                      $.ajax({
                          url: "my-bookmark-delete",
                          method: "POST",
                          traditional: true,
                          data: JSON.stringify(dataToSend),
                          contentType: 'application/json; charset=UTF-8',
                          success: function(result, status, xhr) {
                                resolve(result);
                          },
                          error: function(xhr, status, err) {
                                reject(err);
                          }
                      });
                })
                .then(result => {
                    alert('삭제 완료되었습니다');
                    $('.bookmark-list-container').load('my-bookmark', function(){
                            phoneNumberFormat();
                    });
                })
                .catch(err => {
                    alert("삭제 실패");
                });
            }
        } else {
            alert('항목이 선택되지 않았습니다');
        }
    });

    // 체크한 사람 메일발송
    $('#send-mail-btn').on('click', function(e) {
          e.preventDefault();
          e.stopPropagation();

          let dataToSend = [];
          let count = 0;
          $('#bookmark-table tbody tr').each(function() {
                let $row = $(this);
                let $checkbox = $row.find('.book-check');
                if($checkbox.is(':checked')) {
                      count++;
                      let empName = $row.find('td').eq(2).text();
                      let email = $row.find('td').eq(5).text();
                      dataToSend.push({
                          emails: email,
                          empName : empName,
                      });
                }
          });

          if(count > 0) {
                // `empName`을 배열로 수집
                let names = dataToSend.map(item => item.empName);

                let confirmMessage;

                // 1명이면
                if (dataToSend.length === 1) {
                    confirmMessage = names[0] + '님에게 이메일을 전송하시겠습니까?';
                } else {
                    // 2명 이상부터
                    let namesString = names.join(', ');
                    confirmMessage = namesString + '님에게 이메일을 전송하시겠습니까?';
                }

                if(confirm(confirmMessage)){
                    const emailFromArray = dataToSend.map(item => item.emails);
                    const emailFromQuery = emailFromArray.join(',');
                    location.href = '/mailbox/write?emails=' + encodeURIComponent(emailFromQuery);
                }
          } else {
                alert('항목이 선택되지 않았습니다');
          }
    });

    // Pagination 버튼 Ajax - 즐겨찾기
    $('.bookmark-list-container').on('click', '.page-item', function(e) {
        let pageNo = $('.bookmark-list-container .paging').data('pageno');
        const lastPageNo = Math.floor(($('.bookmark-list-container .paging').data('datacount') / 5) + (($('.bookmark-list-container .paging').data('datacount') % 5) > 0 ? 1 : 0));
        if (/«/.test($(this).text())) pageNo = 1;
        if ($(this).text() == 'Previous') pageNo = pageNo - 1 < 1 ? 1 : pageNo - 1;
        if ($(this).text() == 'Next') pageNo = pageNo + 1 > lastPageNo ? lastPageNo : pageNo + 1;
        if (/»/.test($(this).text())) pageNo = lastPageNo;
        if (!isNaN($(this).text())) pageNo = $(this).text();

        let option = $('.bookmark-search-option > option:selected').val();
        let bookmarkKeyword = $('#bookmark-search-keyword').val();
        let value = sortName == 'name' ? name : dept;

        $('.bookmark-list-container').load('my-bookmark?pageNo=' + pageNo +
                                             '&option=' + option +
                                              '&keyword=' + bookmarkKeyword +
                                              '&sortName=' + sortName +
                                              '&sortValue=' + value, function(){
                                                        phoneNumberFormat();
                                               });
    });

    // 즐겨찾기 서치
    $('#bookmark-submit-btn').on('click', function(e) {
          e.preventDefault();
          e.stopPropagation();

          let option = $('.bookmark-search-option > option:selected').val();
          let bookmarkKeyword = $('#bookmark-search-keyword').val();

          if(option != 'all' && bookmarkKeyword == '') {
              alert('검색어를 입력해 주세요');
              return;
          }

          $('.bookmark-list-container').load('my-bookmark?pageNo=' + 1 +
                                '&option=' + option +
                                '&keyword=' + bookmarkKeyword, function(){
                                      phoneNumberFormat();
          });
    });

    // 파일함 로드
    $('.my-fileBox-btn').on('click', (e) => {
          $('.my-fileBox-container').load('my-file-box', function(){
              truncateUserFileName();
              $('.file-search-keyword').val('all');
          });
    });

    // userFileName 25자 이상 ...으로 대체
    function truncateUserFileName() {
        let maxLength = 25; // 표시할 최대 글자 수
        $('.my-fileBox-container .userFileName').each(function() {
              let text = $(this).text();
              if (text.length > maxLength) {
                  let truncatedText = text.substr(0, maxLength - 2) + '...';
                  $(this).text(truncatedText);
              }
        });
    }

    // 파일 등록페이지 로드
    $('#file-create-btn').on('click', (e) => {
          $('.my-fileBox-container').load('my-file-create');
    });

    // 파일 등록 저장 버튼
    $('.my-fileBox-container').on('click', '#file-submit-btn', function(e) {
            e.preventDefault();
            e.stopPropagation();

            if($('#note').val() == '') {
                alert('메모를 입력해 주세요');
                return;
            }
            let getFile = $('.my-fileBox-container #attach')[0];
            let file = getFile.files[0];
            if(!file) {
                alert('첨부 파일을 등록해 주세요');
                return;
            }

            let formData = new FormData();
            let fileInput = $('input[name="attach"]')[0];

            if (fileInput.files.length > 0) {
                formData.append('attach', fileInput.files[0]);
            }

            formData.append('empId', $('#empId').val());
            formData.append('note', $('#note').val());
            formData.append('fileSort', $('#file-create-Sort').val());

            if(confirm('파일을 등록하시겠습니까?')){
                  $('#modal-file').modal('hide');
                  new Promise((resolve, reject) => {
                      $.ajax({
                          "url" : "my-file-save",
                          "method" : "POST",
                          "data" : formData,
                          "processData": false,
                          "contentType": false,
                          "success": function(result) {
                              resolve(result);
                          },
                          "error": function(xhr, status, err) {
                              reject(err);
                          }
                      });
                  })
                  .then(result => {
                      if(result === 'success'){
                          alert('파일 등록이 완료되었습니다');
                          $('.my-fileBox-container').load('my-file-box', function(){
                              truncateUserFileName();
                          });
                      } else {
                          alert('파일 등록에 실패하였습니다')
                      }
                  })
                  .catch(err => {
                      alert("파일 등록 에러");
                  });
            }
    });

        // Pagination 버튼 Ajax - 파일함
        $('.my-fileBox-container').on('click', '.page-item', function(e) {
            let pageNo = $('.my-fileBox-container .paging').data('pageno');
            const lastPageNo = Math.floor(($('.my-fileBox-container .paging').data('datacount') / 5) + (($('.my-fileBox-container .paging').data('datacount') % 5) > 0 ? 1 : 0));
            if (/«/.test($(this).text())) pageNo = 1;
            if ($(this).text() == 'Previous') pageNo = pageNo - 1 < 1 ? 1 : pageNo - 1;
            if ($(this).text() == 'Next') pageNo = pageNo + 1 > lastPageNo ? lastPageNo : pageNo + 1;
            if (/»/.test($(this).text())) pageNo = lastPageNo;
            if (!isNaN($(this).text())) pageNo = $(this).text();

            let fileKeyword = $('.file-search-keyword > option:selected').val();

            $('.my-fileBox-container').load('my-file-box?pageNo=' + pageNo + '&keyword=' + fileKeyword, function(){
                   truncateUserFileName();
            });
        });

        // 파일함 구분 버튼
        $('#file-search-submit-btn').on('click', function(e) {
              e.preventDefault();
              e.stopPropagation();

              let fileKeyword = $('.file-search-keyword > option:selected').val();

             $('.my-fileBox-container').load('my-file-box?pageNo=' + 1 + '&keyword=' + fileKeyword, function(){
                    truncateUserFileName();
             });
        });

        $('.my-fileBox-container').on('click', '#file-remove-btn', function(e) {
               if(confirm('해당 파일을 삭제하겠습니까?')) {
                    const fileNo = $(this).data('fileno');

                    commonAjax('userFile-remove', 'GET', { fileNo : fileNo })
                        .then(result => {
                            if(result === 'success') {
                                alert('삭제 완료되었습니다');
                                $('.my-fileBox-container').load('my-file-box', function(){
                                    truncateUserFileName();
                                });
                            } else {
                                alert('파일 삭제에 실패하였습니다');
                            }
                        })
                        .catch(err => {
                            alert('파일 삭제 중 오류가 발생했습니다');
                        })
               }
        });


    // Pagination 버튼 Ajax - 근태현황
        $('.attendance-list-container').on('click', '.page-item', function(e) {
            let pageNo = $('.attendance-list-container .paging').data('pageno');
            const lastPageNo = Math.floor(($('.attendance-list-container .paging').data('datacount') / 5) + (($('.attendance-list-container .paging').data('datacount') % 5) > 0 ? 1 : 0));
            if (/«/.test($(this).text())) pageNo = 1;
            if ($(this).text() == 'Previous') pageNo = pageNo - 1 < 1 ? 1 : pageNo - 1;
            if ($(this).text() == 'Next') pageNo = pageNo + 1 > lastPageNo ? lastPageNo : pageNo + 1;
            if (/»/.test($(this).text())) pageNo = lastPageNo;
            if (!isNaN($(this).text())) pageNo = $(this).text();

            let attdKeyword = $('.attd-search-keyword > option:selected').val();
            let startDate = $('#attd-start-date').val();
            let endDate = $('#attd-end-date').val();
            $('.attendance-list-container').load('my-attendance?pageNo=' + pageNo +
                                                 '&keyword=' + attdKeyword +
                                                 '&startDate=' + startDate +
                                                 '&endDate=' + endDate);
        });

        // 근태현황 n개월 선택 버튼
        $('.attd-month-btn').on('click', function(e){
              switch($(this).val()) {
                  case '1' :
                      $('#attd-start-date').datepicker('setDate', '-1M');
                      break;
                  case '3' :
                      $('#attd-start-date').datepicker('setDate', '-3M');
                      break;
                  case '6' :
                      $('#attd-start-date').datepicker('setDate', '-6M');
                      break;
              }
        });

        // 근태현황 조회 서치 버튼
        $('#attd-submit-btn').on('click', function(e) {
               e.preventDefault();
               e.stopPropagation();

               let attdKeyword = $('.attd-search-keyword > option:selected').val();
               let startDate = $('#attd-start-date').val();
               let endDate = $('#attd-end-date').val();
               //const dateCheck = (/^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/);

              if( startDate == '') {
                  alert('시작 날짜를 선택해 주세요');
                  $('#attd-start-date').focus();
                  return;
              } else if( endDate == '') {
                  alert('종료 날짜를 선택해 주세요');
                  $('#attd-end-date').focus();
                  return;
              }

              $('.attendance-list-container').load('my-attendance?pageNo=' + 1 +
                                                                '&keyword=' + attdKeyword +
                                                                '&startDate=' + startDate +
                                                                '&endDate=' + endDate);
        });

        // 근태 수정요청
        $('.attendance-list-container').on('click', '.request-modify-button', function(e) {
            var row = $(this).closest('tr');
            var empId = row.find('td').eq(0).text();
            var attdDate = $('#attd-attdDate').val();

    //            $('#attd-attdDate').val();

            $('#empId2').val(empId);
            $('#attdDate2').val(attdDate);

            // 모달을 엽니다.
            //$('#modal-attendance-edit').modal('show');
        });
            ////////////////////// 형준 //////////////////////

        $('.attendance-list-container').on('click', '#modifyRequest', function() {
        var empId = $('#empId2').val();
        var attdDate = $('#attdDate2').val().trim();
        var attdDetail = $('#attdDetail').val();

        var data = {
            empId: empId,
            attdDateStr: attdDate,
            attdDetail: attdDetail
        };

        console.log("Sending data:", data); // 전송 데이터 로그 확인

        // AJAX 요청
        $.ajax({
            type: 'POST',
            url: '/pages/attendance/attendance-list',
            data: JSON.stringify(data),
            contentType: 'application/json',
            dataType: 'text',
            success: function(response, status, xhr) {
                console.log('AJAX 성공:', response);
                window.alert('수정 요청 완료');
                $('#modal-attendance-edit').modal('hide');
                location.reload();
            },
            error: function(xhr, status, error) {
                console.log('AJAX 오류:', error);
            }
        });
    });

    // Pagination 버튼 Ajax - 로그인 조회
        $('.login-list-container').on('click', '.page-item', function(e) {
            let pageNo = $('.my-loginAttempt-div .paging').data('pageno');
            const lastPageNo = Math.floor(($('.my-loginAttempt-div .paging').data('datacount') / 5) + (($('.my-loginAttempt-div .paging').data('datacount') % 5) > 0 ? 1 : 0));
            if (/«/.test($(this).text())) pageNo = 1;
            if ($(this).text() == 'Previous') pageNo = pageNo - 1 < 1 ? 1 : pageNo - 1;
            if ($(this).text() == 'Next') pageNo = pageNo + 1 > lastPageNo ? lastPageNo : pageNo + 1;
            if (/»/.test($(this).text())) pageNo = lastPageNo;
            if (!isNaN($(this).text())) pageNo = $(this).text();

            let loginKeyword = $('.login-search-keyword > option:selected').val();
            let startDate = $('#login-start-date').val();
            let endDate = $('#login-end-date').val();
            $('.login-list-container').load('login-attempt?pageNo=' + pageNo +
                                                          '&keyword=' + loginKeyword +
                                                          '&startDate=' + startDate +
                                                          '&endDate=' + endDate);

        });

        // 로그인 조회 n개월 선택 버튼
        $('.login-month-btn').on('click', function(e){
              switch($(this).val()) {
                  case '1' :
                      $('#login-start-date').datepicker('setDate', '-1M');
                      break;
                  case '3' :
                      $('#login-start-date').datepicker('setDate', '-3M');
                      break;
                  case '6' :
                      $('#login-start-date').datepicker('setDate', '-6M');
                      break;
              }
        });

        // 로그인 조회 서치 버튼
        $('#login-submit-btn').on('click', function(e) {
              e.preventDefault();
              e.stopPropagation();

              let loginKeyword = $('.login-search-keyword > option:selected').val();
              let startDate = $('#login-start-date').val();
              let endDate = $('#login-end-date').val();

              if( startDate == '') {
                  alert('시작 날짜를 선택해 주세요');
                  startDate.focus();
                  return;
              } else if( endDate == '') {
                  alert('종료 날짜를 선택해 주세요');
                  endDate.focus();
                  return;
              }

              $('.login-list-container').load('login-attempt?pageNo=' + 1 +
                                                      '&keyword=' + loginKeyword +
                                                      '&startDate=' + startDate +
                                                      '&endDate=' + endDate);
        });



        // 내 정보 (첫 페이지)
        $('.my-info-div').on('click', '#cancel-btn', function(e) {
            e.stopPropagation();
            e.preventDefault();
            history.back();
       });

       // 이미지 파일만 첨부하는 유효성 함수
       function chk_file_type(fileInput) {
               let file_kind = fileInput.value.lastIndexOf('.');
               let file_name = fileInput.value.substring(file_kind+1,fileInput.length);
               let file_type = file_name.toLowerCase();

               let check_file_type=new Array();

               check_file_type=['jpg','png','jpeg','bmp', ''];

               if(check_file_type.indexOf(file_type)==-1){
                    alert('프로필 사진은 이미지 파일만 선택할 수 있습니다');
                    let parent_Obj=fileInput.parentNode
                    let node=parent_Obj.replaceChild(fileInput.cloneNode(true),fileInput);
                    return false;
               }
               return true;
      }

       $('.my-info-div').on('click', '#submit-btn', function(e) {
           e.stopPropagation();
           e.preventDefault();

           const empId = $('#empId').val();
           const phone = $('#empPhone').val();
           const address = $('#UserAdd1').val();
           const addressDetail = $('#UserAdd2').val();
           const empEmail = $('#empEmail').val();

           if(phone == '') {
                 alert('휴대폰 번호를 입력해 주세요');
                 $('#empPhone').focus();
                 return;
           } else if(address == '') {
                  alert('우편번호 찾기를 진행해 주세요');
                  return;
           } else if(addressDetail == '') {
                  alert('나머지 주소를 입력해 주세요');
                  $('#UserAdd2').focus();
                  return;
           } else if(empEmail == '') {
                  alert('이메일을 입력해 주세요');
                  $('#empEmail').focus();
                  return;
           }

           if(phone.length < 11) {
                 alert('휴대폰 번호를 다시 확인해 주세요');
                 $('#empPhone').focus();
                 return;
           }
           const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
           if (!emailRegex.test(empEmail)) {
                alert("'@'를 포함하여 이메일을 작성해 주세요");
                $('#empEmail').focus();
                return;
           }


           let formData = new FormData();
           let fileInput = $('.my-info-div input[name="info-attach"]')[0];

           if(fileInput.files.length > 0) {
              formData.append('attach', fileInput.files[0]);
           }

           formData.append('empId', empId);
           formData.append('empPhone', phone);
           formData.append('empAddress', address);
           formData.append('empAddressDetail', addressDetail);
           formData.append('empEmail', empEmail);


           if (!chk_file_type(fileInput)) {
               return; // 파일 유효성 실패시
           }

           if(confirm('수정 사항을 저장하시겠습니까?')) {
                   new Promise((resolve, reject) => {
                          $.ajax({
                              "url" : "my-information-edit",
                              "data" : formData,
                              "method" : "POST",
                              "data" : formData,
                              "processData": false,
                              "contentType": false,
                              "success" : function(result) {
                                  resolve(result);
                              },
                              "error" : function( xhz, status, err) {
                                  reject(err);
                              }
                          });
                      })
                      .then(result => {
                          if(result === 'success') {
                              alert('정보 수정이 완료되었습니다');
                              location.href = 'my-page';
                          } else {
                              alert('정보 수정에 실패하였습니다');
                          }
                      })
                      .catch(err => {
                          alert('에러구역');
                      });
                   $('information-form').submit();
          }
       });


        // 메모장 삭제
        $('.todo-list').on('click', '.todo-remove-btn', function(e) {
            e.stopPropagation();
            e.preventDefault();

            const scheduleNo = $(this).data('schedule-no');
            console.log(scheduleNo);

            if(confirm('해당 메모를 삭제하시겠습니까?')) {
                  commonAjax('todo-delete', 'GET', { scheduleNo : scheduleNo })
                      .then(result => {
                            if(result === 'success') {
                                alert('삭제 완료되었습니다');
                                $('.todo-list').load('todo-list');
                            } else {
                                alert('삭제 도중 오류가 발생했습니다');
                            }
                      })
                      .catch(err => {
                            if(result === 'success') {
                                alert('삭제 완료되었습니다');
                                $('.todo-list').load('todo-list');
                            } else {
                                alert('삭제 도중 오류가 발생했습니다');
                            }
                      });
            }
        });

      // 메모 등록
      // $('.todo-list').on('click', '#submit-btn', function(e) {
      $('#todo-submit-btn').on('click', function(e) {

            const start = $('#personalScheduleStartDate').val();
            const end = $('#personalScheduleEndDate').val();
            const content = $('#personalScheduleContent').val().trim();
            const empId = $('#empId').val();

            if(start == '' || end == '') {
                  alert('시작일/종료일을 선택해 주세요');
                  return;
            } else if(content == '') {
                  alert('내용을 입력해 주세요');
                  return;
            }

            if(confirm('등록하시겠습니까?')) {
                        commonAjax('todo-add', 'GET', { "personalScheduleStartDate" : start,
                                    "personalScheduleEndDate" : end,
                                    "personalScheduleContent" : content,
                                    "empId" : empId })
                        .then(result => {
                            if(result === 'success') {
                                alert('등록이 완료되었습니다.');
                                $('.modal').modal('hide');
                            } else {
                                alert('등록 도중 오류가 발생했습니다');
                            }
                            // setTimeout(() => $('#todo-list-container').load('todo-list'), 300);
                            $('#todo-list-container').load('todo-list');
                        })
                        .catch(err => {
                            alert('에러구역');
                        });
            }
      });

      // 메모장 정렬
      $('.sortKeyword-btn').on('click',function(e) {
            const keyword = $(this).val();
            console.log(keyword);
            $('.todo-list').load('todo-list?sortKeyword=' + keyword);

      });
  });