describe("schedule", function() {
	  beforeEach(function() {
	      jasmine.Ajax.install();
	    });
	    afterEach(function() {
	      jasmine.Ajax.uninstall();
	    });
	it("check week",function(){ 
        expect(Schedule.checkNextWeek(new Date('2014-06-26'))).toEqual(true); 
    }); 
	
	 it("allows responses to be setup ahead of time", function () {
	      var doneFn = jasmine.createSpy("success");
	      jasmine.Ajax.stubRequest('/pc/schedule/saveEasy.htm').andReturn({
	        "responseText": 'ok'
	      });

	      var xhr = new XMLHttpRequest();
	      xhr.onreadystatechange = function(arguments) {
	        if (this.readyState == this.DONE) {
	          doneFn(this.responseText);
	        }
	      };

	      xhr.open("POST", "/pc/schedule/saveEasy.htm");
	      xhr.send();

	      expect(doneFn).toHaveBeenCalledWith('ok');
	  });
});
