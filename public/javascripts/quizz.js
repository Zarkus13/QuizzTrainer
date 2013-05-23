
$(function() {
    $('#ConfirmButton').click(function() {
        $(this).attr('disabled', 'disabled');
        $('#NextButton').removeAttr('disabled');

        $(this).addClass('disabled');
        $('#NextButton').removeClass('disabled');

        validateQuestion();
    });

    $('#NextButton').click(function() {
        $(this).attr('disabled', 'disabled');
        $('#ConfirmButton').removeAttr('disabled');

        $(this).addClass('disabled');
        $('#ConfirmButton').removeClass('disabled');

        nextQuestion();
    });

    $('#Answers').on(
        'click',
        'li',
        function() {
            $(this).toggleClass('selected');
        }
    );

    // Retrieving first question
    getQuestion();
});


/**
 * Function switching to next question by incrementing currentQuestion and then calling getQuestion()
 */
function nextQuestion() {
    currentQuestion++;

    getQuestion();
}


/**
 * Function retrieving a question based on the current question and on the order of the questions IDs
 */
function getQuestion() {
    $.get(
        '/question/' + questionsIDs[currentQuestion]
    ).done(
        function(json) {
            $('#Question').text(json.text);

            $('#Answers li').remove();
            for(var k in json.answers[0]) {
                var answer = json.answers[0][k];
                var li = $('<li letter="' + answer.letter + '">' + answer.text + '</li>');

                $('#Answers').append(li);
            }
        }
    );
}


/**
 * Function validating the answers selected by the user, and checking if they are correct
 */
function validateQuestion() {
    var answers = "";
    var i = 0;

    $('#Answers li.selected').each(function() {
        if(i > 0) answers += ","

        answers += $(this).attr("letter");
    });

    $.ajax({
        type: 'POST',
        url: '/answer',
        data: {
            id: questionsIDs[currentQuestion],
            answers: answers
        }
    }).done(
        function(json) {
            if(json.success != undefined) {

            }
            else if(json.fail != undefined) {

            }
            else {

            }
        }
    )
}