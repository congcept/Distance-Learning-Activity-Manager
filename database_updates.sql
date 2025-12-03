CREATE TABLE IF NOT EXISTS submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    activity_id INT NOT NULL,
    student_id INT NOT NULL,
    submission_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    content TEXT,
    grade VARCHAR(10),
    feedback TEXT,
    FOREIGN KEY (activity_id) REFERENCES activities (id),
    FOREIGN KEY (student_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users (id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE KEY unique_enrollment (student_id, course_id)
);

-- Add columns for file uploads
ALTER TABLE submissions ADD COLUMN file_path VARCHAR(255);

ALTER TABLE submissions ADD COLUMN original_filename VARCHAR(255);