highest = {period1:[],period2:[],...}
previousperiods = {period1:[], period2:[period1], period3:[period1, period2], ...}

for period in periods:
    for course in period:

        if len(highest[period]) == 2:

            # check if course is higher than lowest utility course in this period
            if course > min(highest[period]):

                # if the course has a prerequisite and the student has not taken it
                if course.preRequisite is not None and !(course.preRequisite not in student.hasTaken):

                    # then check in all previous periods if this prereq is given
                    for period in previousperiods[period]:
                        pass

                else if course.preRequisite in student.hasTaken:
                    # replace new course
                    highest[period].remove(min(highest[period]))
                    highest[period].append(course)

        else:
            highest[period].append(course)

def ReplaceBreakCheck()
